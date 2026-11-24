package com.foodie.dto.response;

public class CustomizationOptionResponse {
    private Long id;
    private String name;
    private Double extraPrice;

    public CustomizationOptionResponse() {}

    public CustomizationOptionResponse(Long id, String name, Double extraPrice) {
        this.id = id;
        this.name = name;
        this.extraPrice = extraPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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