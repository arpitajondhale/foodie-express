package com.foodie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_health_profiles")
public class UserHealthProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_health_conditions", joinColumns = @JoinColumn(name = "profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_name")
    private Set<HealthCondition> conditions = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen_name")
    private Set<Allergen> allergies = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "diet_preference")
    private DietPreference dietPreference = DietPreference.VEG;

    @Column(name = "daily_calorie_target")
    private Integer dailyCalorieTarget = 2000;

    @Column(name = "max_sugar_grams_per_meal")
    private Double maxSugarGramsPerMeal = 15.0;

    @Column(name = "max_sodium_mg_per_meal")
    private Double maxSodiumMgPerMeal = 600.0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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