package com.foodie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nutritional_info")
public class NutritionalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false, unique = true)
    private FoodItem foodItem;

    @Column(nullable = false)
    private Integer calories = 250;

    @Column(name = "protein_grams", nullable = false)
    private Double proteinGrams = 8.0;

    @Column(name = "carbs_grams", nullable = false)
    private Double carbsGrams = 30.0;

    @Column(name = "sugar_grams", nullable = false)
    private Double sugarGrams = 4.0;

    @Column(name = "fat_grams", nullable = false)
    private Double fatGrams = 6.0;

    @Column(name = "saturated_fat_grams", nullable = false)
    private Double saturatedFatGrams = 2.0;

    @Column(name = "fiber_grams", nullable = false)
    private Double fiberGrams = 3.0;

    @Column(name = "sodium_mg", nullable = false)
    private Double sodiumMg = 350.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "glycemic_index", nullable = false)
    private GlycemicIndex glycemicIndex = GlycemicIndex.LOW;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "food_allergens", joinColumns = @JoinColumn(name = "nutritional_info_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen")
    private Set<Allergen> allergens = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "food_suitable_conditions", joinColumns = @JoinColumn(name = "nutritional_info_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_name")
    private Set<HealthCondition> suitableForConditions = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "food_contraindicated_conditions", joinColumns = @JoinColumn(name = "nutritional_info_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_name")
    private Set<HealthCondition> contraindicatedConditions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
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