package com.foodie.dto.response;

import java.util.ArrayList;
import java.util.List;

public class FoodCustomizationResponse {
    private Long id;
    private String groupName;
    private boolean required;
    private List<CustomizationOptionResponse> options = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<CustomizationOptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<CustomizationOptionResponse> options) {
        this.options = options;
    }
}