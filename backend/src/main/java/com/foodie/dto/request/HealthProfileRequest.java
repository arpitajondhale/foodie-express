package com.foodie.dto.request;

import com.foodie.entity.Allergen;
import com.foodie.entity.DietPreference;
import com.foodie.entity.HealthCondition;
import java.util.HashSet;
import java.util.Set;

public class HealthProfileRequest {

    private Set<HealthCondition> conditions = new HashSet<>();
    private Set<Allergen> allergies = new HashSet<>();
    private DietPreference dietPreference = DietPreference.VEG;
    private Integer dailyCalorieTarget = 2000;
    private Double maxSugarGramsPerMeal = 15.0;
    private Double maxSodiumMgPerMeal = 600.0;

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