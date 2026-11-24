package com.foodie.controller;

import com.foodie.dto.request.HealthProfileRequest;
import com.foodie.dto.response.HealthProfileResponse;
import com.foodie.entity.User;
import com.foodie.service.AuthService;
import com.foodie.service.HealthProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-profile")
@Tag(name = "Customer Health Profiles", description = "Endpoints for managing diagnosed medical conditions, allergies, and nutritional limits.")
public class HealthProfileController {

    private final HealthProfileService healthProfileService;
    private final AuthService authService;

    public HealthProfileController(HealthProfileService healthProfileService, AuthService authService) {
        this.healthProfileService = healthProfileService;
        this.authService = authService;
    }

    @GetMapping
    @Operation(summary = "Get the authenticated customer's health profile")
    public ResponseEntity<HealthProfileResponse> getProfile() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(healthProfileService.getProfile(user));
    }

    @PutMapping
    @Operation(summary = "Update diagnosed conditions, food allergies, and daily macro targets")
    public ResponseEntity<HealthProfileResponse> updateProfile(@RequestBody HealthProfileRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(healthProfileService.updateProfile(user, request));
    }
}