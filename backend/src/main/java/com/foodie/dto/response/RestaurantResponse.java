package com.foodie.dto.response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantResponse {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private Set<String> cuisineTypes = new HashSet<>();
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private Double rating;
    private Integer totalRatings;
    private Integer avgPrepTimeMins;
    private boolean open;
    private String fssaiLicense;
    private String imageUrl;
    private List<MenuCategoryResponse> categories = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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

    public Set<String> getCuisineTypes() {
        return cuisineTypes;
    }

    public void setCuisineTypes(Set<String> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Integer getAvgPrepTimeMins() {
        return avgPrepTimeMins;
    }

    public void setAvgPrepTimeMins(Integer avgPrepTimeMins) {
        this.avgPrepTimeMins = avgPrepTimeMins;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getFssaiLicense() {
        return fssaiLicense;
    }

    public void setFssaiLicense(String fssaiLicense) {
        this.fssaiLicense = fssaiLicense;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<MenuCategoryResponse> getCategories() {
        return categories;
    }

    public void setCategories(List<MenuCategoryResponse> categories) {
        this.categories = categories;
    }
}