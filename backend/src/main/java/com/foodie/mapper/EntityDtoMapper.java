package com.foodie.mapper;

import com.foodie.dto.response.*;
import com.foodie.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityDtoMapper {

    public UserSummaryResponse toUserSummary(User user) {
        if (user == null) return null;
        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }

    public HealthProfileResponse toHealthProfileResponse(UserHealthProfile p) {
        if (p == null) return null;
        HealthProfileResponse res = new HealthProfileResponse();
        res.setId(p.getId());
        res.setUserId(p.getUser() != null ? p.getUser().getId() : null);
        res.setConditions(p.getConditions());
        res.setAllergies(p.getAllergies());
        res.setDietPreference(p.getDietPreference());
        res.setDailyCalorieTarget(p.getDailyCalorieTarget());
        res.setMaxSugarGramsPerMeal(p.getMaxSugarGramsPerMeal());
        res.setMaxSodiumMgPerMeal(p.getMaxSodiumMgPerMeal());
        return res;
    }

    public AddressResponse toAddressResponse(Address a) {
        if (a == null) return null;
        AddressResponse res = new AddressResponse();
        res.setId(a.getId());
        res.setLabel(a.getLabel());
        res.setStreet(a.getStreet());
        res.setCity(a.getCity());
        res.setPincode(a.getPincode());
        res.setLatitude(a.getLatitude());
        res.setLongitude(a.getLongitude());
        res.setDefaultAddress(a.isDefaultAddress());
        return res;
    }

    public NutritionalInfoResponse toNutritionalInfoResponse(NutritionalInfo n) {
        if (n == null) return null;
        NutritionalInfoResponse res = new NutritionalInfoResponse();
        res.setId(n.getId());
        res.setCalories(n.getCalories());
        res.setProteinGrams(n.getProteinGrams());
        res.setCarbsGrams(n.getCarbsGrams());
        res.setSugarGrams(n.getSugarGrams());
        res.setFatGrams(n.getFatGrams());
        res.setSaturatedFatGrams(n.getSaturatedFatGrams());
        res.setFiberGrams(n.getFiberGrams());
        res.setSodiumMg(n.getSodiumMg());
        res.setGlycemicIndex(n.getGlycemicIndex());
        res.setAllergens(n.getAllergens());
        res.setSuitableForConditions(n.getSuitableForConditions());
        res.setContraindicatedConditions(n.getContraindicatedConditions());
        return res;
    }

    public CustomizationOptionResponse toCustomizationOptionResponse(CustomizationOption opt) {
        if (opt == null) return null;
        return new CustomizationOptionResponse(opt.getId(), opt.getName(), opt.getExtraPrice());
    }

    public FoodCustomizationResponse toFoodCustomizationResponse(FoodCustomization c) {
        if (c == null) return null;
        FoodCustomizationResponse res = new FoodCustomizationResponse();
        res.setId(c.getId());
        res.setGroupName(c.getGroupName());
        res.setRequired(c.isRequired());
        if (c.getOptions() != null) {
            res.setOptions(c.getOptions().stream().map(this::toCustomizationOptionResponse).collect(Collectors.toList()));
        }
        return res;
    }

    public FoodItemResponse toFoodItemResponse(FoodItem f) {
        if (f == null) return null;
        FoodItemResponse res = new FoodItemResponse();
        res.setId(f.getId());
        res.setRestaurantId(f.getRestaurant() != null ? f.getRestaurant().getId() : null);
        res.setRestaurantName(f.getRestaurant() != null ? f.getRestaurant().getName() : null);
        res.setCategoryId(f.getCategory() != null ? f.getCategory().getId() : null);
        res.setCategoryName(f.getCategory() != null ? f.getCategory().getName() : null);
        res.setName(f.getName());
        res.setDescription(f.getDescription());
        res.setPrice(f.getPrice());
        res.setDiscountPrice(f.getDiscountPrice());
        res.setDietType(f.getDietType());
        res.setSpiceLevel(f.getSpiceLevel());
        res.setAvailable(f.isAvailable());
        res.setImageUrl(f.getImageUrl());
        res.setPreparationTimeMins(f.getPreparationTimeMins());
        res.setAverageRating(f.getAverageRating());
        res.setNutritionalInfo(toNutritionalInfoResponse(f.getNutritionalInfo()));
        if (f.getCustomizations() != null) {
            res.setCustomizations(f.getCustomizations().stream().map(this::toFoodCustomizationResponse).collect(Collectors.toList()));
        }
        return res;
    }

    public MenuCategoryResponse toMenuCategoryResponse(MenuCategory c, boolean includeItems) {
        if (c == null) return null;
        MenuCategoryResponse res = new MenuCategoryResponse();
        res.setId(c.getId());
        res.setRestaurantId(c.getRestaurant() != null ? c.getRestaurant().getId() : null);
        res.setName(c.getName());
        res.setDisplayOrder(c.getDisplayOrder());
        if (includeItems && c.getFoodItems() != null) {
            res.setItems(c.getFoodItems().stream().map(this::toFoodItemResponse).collect(Collectors.toList()));
        }
        return res;
    }

    public RestaurantResponse toRestaurantResponse(Restaurant r, boolean includeCategories) {
        if (r == null) return null;
        RestaurantResponse res = new RestaurantResponse();
        res.setId(r.getId());
        res.setOwnerId(r.getOwnerId());
        res.setName(r.getName());
        res.setDescription(r.getDescription());
        res.setCuisineTypes(r.getCuisineTypes());
        res.setAddress(r.getAddress());
        res.setCity(r.getCity());
        res.setLatitude(r.getLatitude());
        res.setLongitude(r.getLongitude());
        res.setRating(r.getRating());
        res.setTotalRatings(r.getTotalRatings());
        res.setAvgPrepTimeMins(r.getAvgPrepTimeMins());
        res.setOpen(r.isOpen());
        res.setFssaiLicense(r.getFssaiLicense());
        res.setImageUrl(r.getImageUrl());
        if (includeCategories && r.getCategories() != null) {
            res.setCategories(r.getCategories().stream().map(c -> toMenuCategoryResponse(c, true)).collect(Collectors.toList()));
        }
        return res;
    }

    public CouponResponse toCouponResponse(Coupon c) {
        if (c == null) return null;
        CouponResponse res = new CouponResponse();
        res.setId(c.getId());
        res.setCode(c.getCode());
        res.setDiscountType(c.getDiscountType());
        res.setDiscountValue(c.getDiscountValue());
        res.setMinOrderAmount(c.getMinOrderAmount());
        res.setMaxDiscount(c.getMaxDiscount());
        res.setValidUntil(c.getValidUntil());
        res.setActive(c.isActive());
        return res;
    }

    public OrderItemResponse toOrderItemResponse(OrderItem i) {
        if (i == null) return null;
        OrderItemResponse res = new OrderItemResponse();
        res.setId(i.getId());
        res.setFoodItemId(i.getFoodItemId());
        res.setFoodName(i.getFoodName());
        res.setPrice(i.getPrice());
        res.setQuantity(i.getQuantity());
        res.setCustomizationDetails(i.getCustomizationDetails());
        res.setSubtotal(i.getSubtotal());
        return res;
    }

    public OrderResponse toOrderResponse(Order o) {
        if (o == null) return null;
        OrderResponse res = new OrderResponse();
        res.setId(o.getId());
        res.setOrderNumber(o.getOrderNumber());
        if (o.getCustomer() != null) {
            res.setCustomerId(o.getCustomer().getId());
            res.setCustomerName(o.getCustomer().getName());
            res.setCustomerPhone(o.getCustomer().getPhone());
        }
        if (o.getRestaurant() != null) {
            res.setRestaurantId(o.getRestaurant().getId());
            res.setRestaurantName(o.getRestaurant().getName());
        }
        if (o.getDeliveryPartner() != null) {
            res.setDeliveryPartnerId(o.getDeliveryPartner().getId());
            res.setDeliveryPartnerName(o.getDeliveryPartner().getName());
        }
        res.setDeliveryAddressSnapshot(o.getDeliveryAddressSnapshot());
        if (o.getItems() != null) {
            res.setItems(o.getItems().stream().map(this::toOrderItemResponse).collect(Collectors.toList()));
        }
        res.setItemTotal(o.getItemTotal());
        res.setPackagingFee(o.getPackagingFee());
        res.setDeliveryFee(o.getDeliveryFee());
        res.setGstAmount(o.getGstAmount());
        res.setDiscountAmount(o.getDiscountAmount());
        res.setGrandTotal(o.getGrandTotal());
        res.setPaymentMethod(o.getPaymentMethod());
        res.setPaymentStatus(o.getPaymentStatus());
        res.setOrderStatus(o.getOrderStatus());
        res.setCancellationReason(o.getCancellationReason());
        res.setEstimatedDeliveryTime(o.getEstimatedDeliveryTime());
        res.setCreatedAt(o.getCreatedAt());
        return res;
    }

    public ReviewResponse toReviewResponse(Review r) {
        if (r == null) return null;
        ReviewResponse res = new ReviewResponse();
        res.setId(r.getId());
        res.setOrderId(r.getOrder() != null ? r.getOrder().getId() : null);
        if (r.getUser() != null) {
            res.setUserId(r.getUser().getId());
            res.setUserName(r.getUser().getName());
        }
        res.setRestaurantId(r.getRestaurant() != null ? r.getRestaurant().getId() : null);
        res.setRating(r.getRating());
        res.setComment(r.getComment());
        res.setCreatedAt(r.getCreatedAt());
        return res;
    }
}