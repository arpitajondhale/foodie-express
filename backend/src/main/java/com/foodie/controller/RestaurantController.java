package com.foodie.controller;

import com.foodie.dto.response.MenuCategoryResponse;
import com.foodie.dto.response.RestaurantResponse;
import com.foodie.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurants", description = "Multi-restaurant discovery, menu exploration, and operational controls.")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    @Operation(summary = "Search and list all active restaurants by keyword or city")
    public ResponseEntity<List<RestaurantResponse>> getRestaurants(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String city) {
        return ResponseEntity.ok(restaurantService.getRestaurants(query, city));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed restaurant information and full categorized menu")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @GetMapping("/{id}/menu")
    @Operation(summary = "Get categorized menu with macro nutritional data and customizations")
    public ResponseEntity<List<MenuCategoryResponse>> getRestaurantMenu(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantMenu(id));
    }

    @PatchMapping("/{id}/toggle-open")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    @Operation(summary = "Toggle restaurant operational state (Open/Closed)")
    public ResponseEntity<RestaurantResponse> toggleRestaurantOpen(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.toggleRestaurantOpen(id));
    }
}