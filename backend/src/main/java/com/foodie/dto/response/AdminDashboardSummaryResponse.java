package com.foodie.dto.response;

public class AdminDashboardSummaryResponse {
    private Double totalGmv = 0.0;
    private Long activeOrders = 0L;
    private Long totalOrders = 0L;
    private Long totalUsers = 0L;
    private Long activeRestaurants = 0L;
    private Long activeCoupons = 0L;

    public Double getTotalGmv() {
        return totalGmv;
    }

    public void setTotalGmv(Double totalGmv) {
        this.totalGmv = totalGmv;
    }

    public Long getActiveOrders() {
        return activeOrders;
    }

    public void setActiveOrders(Long activeOrders) {
        this.activeOrders = activeOrders;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveRestaurants() {
        return activeRestaurants;
    }

    public void setActiveRestaurants(Long activeRestaurants) {
        this.activeRestaurants = activeRestaurants;
    }

    public Long getActiveCoupons() {
        return activeCoupons;
    }

    public void setActiveCoupons(Long activeCoupons) {
        this.activeCoupons = activeCoupons;
    }
}