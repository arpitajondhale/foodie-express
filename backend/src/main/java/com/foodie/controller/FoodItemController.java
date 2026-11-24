package com.foodie.controller;

import com.foodie.dto.response.FoodItemResponse;
import com.foodie.entity.DietType;
import com.foodie.service.FoodItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@Tag(name = "Food Items & Menu Catalog", description = "Endpoints for discovering dishes, macros, and availability toggles.")
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @GetMapping
    @Operation(summary = "Search dishes with multi-criteria filters including 'Safe For My Health' profile filter")
    public ResponseEntity<List<FoodItemResponse>> getFoodItems(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) DietType dietType,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean healthyOnly,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(foodItemService.getFoodItems(restaurantId, categoryId, query, dietType, maxPrice, healthyOnly, userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single food item details including embedded nutritional macro profile")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        return ResponseEntity.ok(foodItemService.getFoodItemById(id));
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    @Operation(summary = "Toggle dish stock availability (In Stock / Sold Out)")
    public ResponseEntity<FoodItemResponse> updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        return ResponseEntity.ok(foodItemService.updateAvailability(id, available));
    }
}