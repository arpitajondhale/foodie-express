package com.foodie.service;

import com.foodie.dto.response.FoodItemResponse;
import com.foodie.entity.DietType;
import com.foodie.entity.FoodItem;
import com.foodie.entity.UserHealthProfile;
import com.foodie.exception.ResourceNotFoundException;
import com.foodie.mapper.EntityDtoMapper;
import com.foodie.repository.FoodItemRepository;
import com.foodie.repository.UserHealthProfileRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;
    private final UserHealthProfileRepository healthProfileRepository;
    private final HealthGuardrailService healthGuardrailService;
    private final EntityDtoMapper mapper;

    public FoodItemService(FoodItemRepository foodItemRepository,
                           UserHealthProfileRepository healthProfileRepository,
                           HealthGuardrailService healthGuardrailService,
                           EntityDtoMapper mapper) {
        this.foodItemRepository = foodItemRepository;
        this.healthProfileRepository = healthProfileRepository;
        this.healthGuardrailService = healthGuardrailService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<FoodItemResponse> getFoodItems(Long restaurantId,
                                               Long categoryId,
                                               String query,
                                               DietType dietType,
                                               Double maxPrice,
                                               Boolean healthyOnly,
                                               Long userId) {
        List<FoodItem> items;
        if (restaurantId != null) {
            items = foodItemRepository.findByRestaurantId(restaurantId);
        } else if (categoryId != null) {
            items = foodItemRepository.findByCategoryId(categoryId);
        } else {
            items = foodItemRepository.findAll();
        }

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            items = items.stream().filter(f ->
                    f.getName().toLowerCase().contains(q) ||
                    (f.getDescription() != null && f.getDescription().toLowerCase().contains(q)) ||
                    (f.getCategory() != null && f.getCategory().getName().toLowerCase().contains(q))
            ).collect(Collectors.toList());
        }

        if (dietType != null) {
            items = items.stream().filter(f -> f.getDietType() == dietType).collect(Collectors.toList());
        }

        if (maxPrice != null && maxPrice > 0) {
            items = items.stream().filter(f -> f.getPrice() <= maxPrice).collect(Collectors.toList());
        }

        if (Boolean.TRUE.equals(healthyOnly) && userId != null) {
            Optional<UserHealthProfile> profileOpt = healthProfileRepository.findByUserId(userId);
            if (profileOpt.isPresent()) {
                UserHealthProfile profile = profileOpt.get();
                items = items.stream()
                        .filter(f -> healthGuardrailService.isItemSafeForProfile(f, profile))
                        .collect(Collectors.toList());
            }
        }

        return items.stream().map(mapper::toFoodItemResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FoodItemResponse getFoodItemById(Long id) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found: " + id));
        return mapper.toFoodItemResponse(item);
    }

    @CacheEvict(value = "restaurant-menus", allEntries = true)
    public FoodItemResponse updateAvailability(Long id, boolean available) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found: " + id));
        item.setAvailable(available);
        item = foodItemRepository.save(item);
        return mapper.toFoodItemResponse(item);
    }
}