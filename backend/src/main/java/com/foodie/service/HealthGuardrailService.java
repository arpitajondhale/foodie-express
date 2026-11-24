package com.foodie.service;

import com.foodie.dto.response.FoodItemResponse;
import com.foodie.dto.response.HealthGuardrailWarningResponse;
import com.foodie.entity.*;
import com.foodie.mapper.EntityDtoMapper;
import com.foodie.repository.FoodItemRepository;
import com.foodie.repository.UserHealthProfileRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HealthGuardrailService {

    private final FoodItemRepository foodItemRepository;
    private final UserHealthProfileRepository healthProfileRepository;
    private final EntityDtoMapper mapper;

    public HealthGuardrailService(FoodItemRepository foodItemRepository,
                                  UserHealthProfileRepository healthProfileRepository,
                                  EntityDtoMapper mapper) {
        this.foodItemRepository = foodItemRepository;
        this.healthProfileRepository = healthProfileRepository;
        this.mapper = mapper;
    }

    public List<String> evaluateItemCompatibility(FoodItem item, UserHealthProfile profile) {
        List<String> warnings = new ArrayList<>();
        if (item == null || profile == null) return warnings;

        NutritionalInfo nut = item.getNutritionalInfo();
        if (nut == null) return warnings;

        // 1. Check Allergies
        if (profile.getAllergies() != null && !profile.getAllergies().isEmpty()) {
            for (Allergen userAllergy : profile.getAllergies()) {
                if (nut.getAllergens() != null && nut.getAllergens().contains(userAllergy)) {
                    warnings.add("Contains Allergen: " + userAllergy.name() + " (matches your diagnosed allergy)");
                }
            }
        }

        // 2. Check Medical Conditions
        if (profile.getConditions() != null && !profile.getConditions().isEmpty()) {
            double maxSugar = profile.getMaxSugarGramsPerMeal() != null ? profile.getMaxSugarGramsPerMeal() : 15.0;
            double maxSodium = profile.getMaxSodiumMgPerMeal() != null ? profile.getMaxSodiumMgPerMeal() : 600.0;

            for (HealthCondition condition : profile.getConditions()) {
                switch (condition) {
                    case DIABETES -> {
                        if (nut.getContraindicatedConditions() != null && nut.getContraindicatedConditions().contains(HealthCondition.DIABETES)) {
                            warnings.add("Contraindicated for Diabetes: high glycemic load");
                        } else if (nut.getSugarGrams() > maxSugar) {
                            warnings.add(String.format("High Sugar (%.1fg): Exceeds your diabetic meal safety threshold (%.1fg)", nut.getSugarGrams(), maxSugar));
                        }
                        if (nut.getGlycemicIndex() == GlycemicIndex.HIGH) {
                            warnings.add("High Glycemic Index: triggers sharp post-meal blood glucose spikes");
                        }
                    }
                    case HYPERTENSION -> {
                        if (nut.getContraindicatedConditions() != null && nut.getContraindicatedConditions().contains(HealthCondition.HYPERTENSION)) {
                            warnings.add("Contraindicated for Hypertension / High BP: elevated sodium formulation");
                        } else if (nut.getSodiumMg() > maxSodium) {
                            warnings.add(String.format("High Sodium (%.0fmg): Exceeds your hypertension limit (%.0fmg)", nut.getSodiumMg(), maxSodium));
                        }
                    }
                    case HEART_DISEASE -> {
                        if (nut.getContraindicatedConditions() != null && nut.getContraindicatedConditions().contains(HealthCondition.HEART_DISEASE)) {
                            warnings.add("Contraindicated for Heart Disease: elevated saturated fats and sodium");
                        } else if (nut.getSaturatedFatGrams() > 4.0 || nut.getSodiumMg() > maxSodium) {
                            warnings.add("High Saturated Fat / Sodium: cardiovascular strain warning");
                        }
                    }
                    case PCOS -> {
                        if (nut.getSugarGrams() > 14.0 || nut.getGlycemicIndex() == GlycemicIndex.HIGH) {
                            warnings.add("High Simple Sugar / GI: aggravates insulin resistance associated with PCOS");
                        }
                    }
                    case CELIAC_GLUTEN_INTOLERANCE -> {
                        if (nut.getAllergens() != null && nut.getAllergens().contains(Allergen.GLUTEN)) {
                            warnings.add("Contains Gluten: triggers intestinal inflammatory reaction in Celiac patients");
                        }
                    }
                    case LACTOSE_INTOLERANCE -> {
                        if (nut.getAllergens() != null && nut.getAllergens().contains(Allergen.DAIRY)) {
                            warnings.add("Contains Dairy / Lactose: may cause digestive distress");
                        }
                    }
                    case KIDNEY_FRIENDLY -> {
                        if (nut.getSodiumMg() > 500.0 || nut.getProteinGrams() > 35.0) {
                            warnings.add("High Sodium / Excess Protein: creates renal filtration strain");
                        }
                    }
                    default -> {}
                }
            }
        }

        // 3. Check Diet Preference
        if (profile.getDietPreference() != null) {
            DietPreference pref = profile.getDietPreference();
            DietType itemDiet = item.getDietType();

            if (pref == DietPreference.VEG && itemDiet == DietType.NON_VEG) {
                warnings.add("Contains meat / non-vegetarian ingredients (conflicts with your Veg preference)");
            } else if (pref == DietPreference.VEGAN && itemDiet != DietType.VEGAN) {
                warnings.add("Contains non-vegan ingredients / animal derivatives");
            } else if (pref == DietPreference.JAIN && itemDiet != DietType.VEG) {
                warnings.add("Not suitable for Jain dietary preference");
            }
        }

        return warnings;
    }

    public boolean isItemSafeForProfile(FoodItem item, UserHealthProfile profile) {
        if (profile == null) return true;
        return evaluateItemCompatibility(item, profile).isEmpty();
    }

    public HealthGuardrailWarningResponse evaluateCartItem(Long foodItemId, Long userId) {
        Optional<FoodItem> itemOpt = foodItemRepository.findById(foodItemId);
        if (itemOpt.isEmpty()) {
            return new HealthGuardrailWarningResponse(false, foodItemId, "Unknown Item", List.of(), List.of());
        }

        FoodItem item = itemOpt.get();
        if (userId == null) {
            return new HealthGuardrailWarningResponse(false, item.getId(), item.getName(), List.of(), List.of());
        }

        Optional<UserHealthProfile> profileOpt = healthProfileRepository.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            return new HealthGuardrailWarningResponse(false, item.getId(), item.getName(), List.of(), List.of());
        }

        UserHealthProfile profile = profileOpt.get();
        List<String> warnings = evaluateItemCompatibility(item, profile);

        if (warnings.isEmpty()) {
            return new HealthGuardrailWarningResponse(false, item.getId(), item.getName(), List.of(), List.of());
        }

        // Find healthy alternatives from the same restaurant
        List<FoodItem> restaurantItems = foodItemRepository.findByRestaurantId(item.getRestaurant().getId());
        List<FoodItemResponse> alternatives = restaurantItems.stream()
                .filter(alt -> !alt.getId().equals(item.getId()) && alt.isAvailable())
                .filter(alt -> isItemSafeForProfile(alt, profile))
                .limit(3)
                .map(mapper::toFoodItemResponse)
                .collect(Collectors.toList());

        return new HealthGuardrailWarningResponse(true, item.getId(), item.getName(), warnings, alternatives);
    }

    @Cacheable(value = "dietary-tags", key = "#item.id")
    public List<String> getDietaryTags(FoodItem item) {
        List<String> tags = new ArrayList<>();
        if (item == null || item.getNutritionalInfo() == null) return tags;

        NutritionalInfo nut = item.getNutritionalInfo();
        if (nut.getSugarGrams() <= 5.0 && nut.getGlycemicIndex() == GlycemicIndex.LOW) {
            tags.add("Diabetic Safe");
        }
        if (nut.getSodiumMg() <= 350.0) {
            tags.add("Low Sodium");
        }
        if (nut.getProteinGrams() >= 14.0) {
            tags.add("High Protein");
        }
        if (nut.getAllergens() == null || !nut.getAllergens().contains(Allergen.GLUTEN)) {
            tags.add("Gluten-Free");
        }
        if (nut.getSaturatedFatGrams() <= 2.0 && nut.getSodiumMg() <= 400.0) {
            tags.add("Heart Healthy");
        }
        if (nut.getFiberGrams() >= 5.0) {
            tags.add("High Fiber");
        }
        return tags;
    }
}