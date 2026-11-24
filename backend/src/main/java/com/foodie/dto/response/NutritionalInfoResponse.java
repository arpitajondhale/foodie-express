package com.foodie.dto.response;

import com.foodie.entity.Allergen;
import com.foodie.entity.GlycemicIndex;
import com.foodie.entity.HealthCondition;
import java.util.Set;

public class NutritionalInfoResponse {
    private Long id;
    private Integer calories;
    private Double proteinGrams;
    private Double carbsGrams;
    private Double sugarGrams;
    private Double fatGrams;
    private Double saturatedFatGrams;
    private Double fiberGrams;
    private Double sodiumMg;
    private GlycemicIndex glycemicIndex;
    private Set<Allergen> allergens;
    private Set<HealthCondition> suitableForConditions;
    private Set<HealthCondition> contraindicatedConditions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Double getProteinGrams() {
        return proteinGrams;
    }

    public void setProteinGrams(Double proteinGrams) {
        this.proteinGrams = proteinGrams;
    }

    public Double getCarbsGrams() {
        return carbsGrams;
    }

    public void setCarbsGrams(Double carbsGrams) {
        this.carbsGrams = carbsGrams;
    }

    public Double getSugarGrams() {
        return sugarGrams;
    }

    public void setSugarGrams(Double sugarGrams) {
        this.sugarGrams = sugarGrams;
    }

    public Double getFatGrams() {
        return fatGrams;
    }

    public void setFatGrams(Double fatGrams) {
        this.fatGrams = fatGrams;
    }

    public Double getSaturatedFatGrams() {
        return saturatedFatGrams;
    }

    public void setSaturatedFatGrams(Double saturatedFatGrams) {
        this.saturatedFatGrams = saturatedFatGrams;
    }

    public Double getFiberGrams() {
        return fiberGrams;
    }

    public void setFiberGrams(Double fiberGrams) {
        this.fiberGrams = fiberGrams;
    }

    public Double getSodiumMg() {
        return sodiumMg;
    }

    public void setSodiumMg(Double sodiumMg) {
        this.sodiumMg = sodiumMg;
    }

    public GlycemicIndex getGlycemicIndex() {
        return glycemicIndex;
    }

    public void setGlycemicIndex(GlycemicIndex glycemicIndex) {
        this.glycemicIndex = glycemicIndex;
    }

    public Set<Allergen> getAllergens() {
        return allergens;
    }

    public void setAllergens(Set<Allergen> allergens) {
        this.allergens = allergens;
    }

    public Set<HealthCondition> getSuitableForConditions() {
        return suitableForConditions;
    }

    public void setSuitableForConditions(Set<HealthCondition> suitableForConditions) {
        this.suitableForConditions = suitableForConditions;
    }

    public Set<HealthCondition> getContraindicatedConditions() {
        return contraindicatedConditions;
    }

    public void setContraindicatedConditions(Set<HealthCondition> contraindicatedConditions) {
        this.contraindicatedConditions = contraindicatedConditions;
    }
}