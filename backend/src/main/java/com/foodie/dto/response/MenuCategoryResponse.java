package com.foodie.dto.response;

import java.util.ArrayList;
import java.util.List;

public class MenuCategoryResponse {
    private Long id;
    private Long restaurantId;
    private String name;
    private Integer displayOrder;
    private List<FoodItemResponse> items = new ArrayList<>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<FoodItemResponse> getItems() {
        return items;
    }

    public void setItems(List<FoodItemResponse> items) {
        this.items = items;
    }
}