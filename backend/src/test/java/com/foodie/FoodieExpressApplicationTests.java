package com.foodie;

import com.foodie.dto.response.HealthGuardrailWarningResponse;
import com.foodie.entity.*;
import com.foodie.repository.FoodItemRepository;
import com.foodie.repository.UserRepository;
import com.foodie.service.AuthService;
import com.foodie.service.HealthGuardrailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class FoodieExpressApplicationTests {

    @Autowired
    private HealthGuardrailService healthGuardrailService;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Application context loads and seeded users exist")
    void contextLoads() {
        assertTrue(userRepository.count() >= 4, "Should have at least 4 seeded demo accounts");
        assertTrue(userRepository.findByEmail("customer@foodie.com").isPresent(), "Customer should be seeded");
        assertTrue(userRepository.findByEmail("owner@foodie.com").isPresent(), "Restaurant owner should be seeded");
        assertTrue(userRepository.findByEmail("rider@foodie.com").isPresent(), "Delivery partner should be seeded");
        assertTrue(userRepository.findByEmail("admin@foodie.com").isPresent(), "Admin should be seeded");
    }

    @Test
    @DisplayName("Health Guardrail detects Diabetic conflict on high-sugar items")
    void testDiabeticGuardrailWarning() {
        UserHealthProfile diabeticProfile = new UserHealthProfile();
        diabeticProfile.setConditions(Set.of(HealthCondition.DIABETES));
        diabeticProfile.setMaxSugarGramsPerMeal(10.0);

        FoodItem sweetDish = new FoodItem();
        sweetDish.setName("Gulab Jamun");
        NutritionalInfo nut = new NutritionalInfo();
        nut.setSugarGrams(35.0);
        nut.setGlycemicIndex(GlycemicIndex.HIGH);
        nut.setContraindicatedConditions(Set.of(HealthCondition.DIABETES));
        sweetDish.setNutritionalInfo(nut);

        List<String> warnings = healthGuardrailService.evaluateItemCompatibility(sweetDish, diabeticProfile);
        assertFalse(warnings.isEmpty(), "Diabetic profile must trigger warnings for high sugar dish");
        assertTrue(warnings.stream().anyMatch(w -> w.toLowerCase().contains("diabetes") || w.toLowerCase().contains("sugar")));
    }

    @Test
    @DisplayName("Health Guardrail flags diagnosed nut allergies")
    void testAllergenGuardrailWarning() {
        UserHealthProfile allergyProfile = new UserHealthProfile();
        allergyProfile.setAllergies(Set.of(Allergen.NUTS));

        FoodItem nutDish = new FoodItem();
        nutDish.setName("Pistachio Kulfi");
        NutritionalInfo nut = new NutritionalInfo();
        nut.setAllergens(Set.of(Allergen.NUTS, Allergen.DAIRY));
        nutDish.setNutritionalInfo(nut);

        List<String> warnings = healthGuardrailService.evaluateItemCompatibility(nutDish, allergyProfile);
        assertFalse(warnings.isEmpty(), "Nut allergy must trigger warning");
        assertTrue(warnings.stream().anyMatch(w -> w.contains("NUTS")));
    }
}