package com.foodie.controller;

import com.foodie.dto.response.HealthGuardrailWarningResponse;
import com.foodie.entity.FoodItem;
import com.foodie.repository.FoodItemRepository;
import com.foodie.service.HealthGuardrailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-guardrail")
@Tag(name = "Health Guardrail & Medical Condition Engine", description = "Core intelligence engine evaluating dish compatibility and cart advisories.")
public class HealthGuardrailController {

    private final HealthGuardrailService healthGuardrailService;
    private final FoodItemRepository foodItemRepository;

    public HealthGuardrailController(HealthGuardrailService healthGuardrailService, FoodItemRepository foodItemRepository) {
        this.healthGuardrailService = healthGuardrailService;
        this.foodItemRepository = foodItemRepository;
    }

    @GetMapping("/evaluate")
    @Operation(summary = "Real-time evaluation of dish against customer health profile, returning warnings & safe alternatives")
    public ResponseEntity<HealthGuardrailWarningResponse> evaluateCartItem(
            @RequestParam Long foodItemId,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(healthGuardrailService.evaluateCartItem(foodItemId, userId));
    }

    @GetMapping("/dietary-tags/{foodId}")
    @Operation(summary = "Get computed health tags (Diabetic Safe, Low Sodium, High Protein, Gluten-Free, etc.)")
    public ResponseEntity<List<String>> getDietaryTags(@PathVariable Long foodId) {
        FoodItem item = foodItemRepository.findById(foodId).orElse(null);
        if (item == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(healthGuardrailService.getDietaryTags(item));
    }
}