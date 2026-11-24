package com.foodie.dto.response;

import java.util.ArrayList;
import java.util.List;

public class HealthGuardrailWarningResponse {
    private boolean hasWarning;
    private Long foodItemId;
    private String foodName;
    private List<String> warningReasons = new ArrayList<>();
    private List<FoodItemResponse> suggestedAlternatives = new ArrayList<>();

    public HealthGuardrailWarningResponse() {}

    public HealthGuardrailWarningResponse(boolean hasWarning, Long foodItemId, String foodName, List<String> warningReasons, List<FoodItemResponse> suggestedAlternatives) {
        this.hasWarning = hasWarning;
        this.foodItemId = foodItemId;
        this.foodName = foodName;
        this.warningReasons = warningReasons;
        this.suggestedAlternatives = suggestedAlternatives;
    }

    public boolean isHasWarning() {
        return hasWarning;
    }

    public void setHasWarning(boolean hasWarning) {
        this.hasWarning = hasWarning;
    }

    public Long getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(Long foodItemId) {
        this.foodItemId = foodItemId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public List<String> getWarningReasons() {
        return warningReasons;
    }

    public void setWarningReasons(List<String> warningReasons) {
        this.warningReasons = warningReasons;
    }

    public List<FoodItemResponse> getSuggestedAlternatives() {
        return suggestedAlternatives;
    }

    public void setSuggestedAlternatives(List<FoodItemResponse> suggestedAlternatives) {
        this.suggestedAlternatives = suggestedAlternatives;
    }
}