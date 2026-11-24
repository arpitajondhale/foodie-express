package com.foodie.dto.response;

import com.foodie.entity.Allergen;
import com.foodie.entity.DietPreference;
import com.foodie.entity.HealthCondition;
import java.util.Set;

public class HealthProfileResponse {
    private Long id;
    private Long userId;
    private Set<HealthCondition> conditions;
    private Set<Allergen> allergies;
    private DietPreference dietPreference;
    private Integer dailyCalorieTarget;
    private Double maxSugarGramsPerMeal;
    private Double maxSodiumMgPerMeal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<HealthCondition> getConditions() {
        return conditions;
    }

    public void setConditions(Set<HealthCondition> conditions) {
        this.conditions = conditions;
    }

    public Set<Allergen> getAllergies() {
        return allergies;
    }

    public void setAllergies(Set<Allergen> allergies) {
        this.allergies = allergies;
    }

    public DietPreference getDietPreference() {
        return dietPreference;
    }

    public void setDietPreference(DietPreference dietPreference) {
        this.dietPreference = dietPreference;
    }

    public Integer getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }

    public void setDailyCalorieTarget(Integer dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
    }

    public Double getMaxSugarGramsPerMeal() {
        return maxSugarGramsPerMeal;
    }

    public void setMaxSugarGramsPerMeal(Double maxSugarGramsPerMeal) {
        this.maxSugarGramsPerMeal = maxSugarGramsPerMeal;
    }

    public Double getMaxSodiumMgPerMeal() {
        return maxSodiumMgPerMeal;
    }

    public void setMaxSodiumMgPerMeal(Double maxSodiumMgPerMeal) {
        this.maxSodiumMgPerMeal = maxSodiumMgPerMeal;
    }
}