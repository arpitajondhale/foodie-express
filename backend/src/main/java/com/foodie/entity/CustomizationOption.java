package com.foodie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "customization_options")
public class CustomizationOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customization_id", nullable = false)
    private FoodCustomization foodCustomization;

    @Column(nullable = false)
    private String name;

    @Column(name = "extra_price", nullable = false)
    private Double extraPrice = 0.0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FoodCustomization getFoodCustomization() {
        return foodCustomization;
    }

    public void setFoodCustomization(FoodCustomization foodCustomization) {
        this.foodCustomization = foodCustomization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(Double extraPrice) {
        this.extraPrice = extraPrice;
    }
}