package com.foodie.dto.response;

import com.foodie.entity.DietType;
import com.foodie.entity.SpiceLevel;
import java.util.ArrayList;
import java.util.List;

public class FoodItemResponse {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private Double price;
    private Double discountPrice;
    private DietType dietType;
    private SpiceLevel spiceLevel;
    private boolean available;
    private String imageUrl;
    private Integer preparationTimeMins;
    private Double averageRating;
    private NutritionalInfoResponse nutritionalInfo;
    private List<FoodCustomizationResponse> customizations = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public DietType getDietType() {
        return dietType;
    }

    public void setDietType(DietType dietType) {
        this.dietType = dietType;
    }

    public SpiceLevel getSpiceLevel() {
        return spiceLevel;
    }

    public void setSpiceLevel(SpiceLevel spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getPreparationTimeMins() {
        return preparationTimeMins;
    }

    public void setPreparationTimeMins(Integer preparationTimeMins) {
        this.preparationTimeMins = preparationTimeMins;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public NutritionalInfoResponse getNutritionalInfo() {
        return nutritionalInfo;
    }

    public void setNutritionalInfo(NutritionalInfoResponse nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    public List<FoodCustomizationResponse> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(List<FoodCustomizationResponse> customizations) {
        this.customizations = customizations;
    }
}