package com.foodie.controller;

import com.foodie.dto.response.AdminDashboardSummaryResponse;
import com.foodie.dto.response.UserSummaryResponse;
import com.foodie.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Platform Administration", description = "Privileged Super Admin console for metrics, user management, and platform oversight.")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get platform-wide KPI summary (GMV, active orders, total restaurants, user counts)")
    public ResponseEntity<AdminDashboardSummaryResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardSummary());
    }

    @GetMapping("/users")
    @Operation(summary = "List all registered platform users and their roles")
    public ResponseEntity<List<UserSummaryResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{id}/toggle-active")
    @Operation(summary = "Activate or suspend a user account")
    public ResponseEntity<UserSummaryResponse> toggleUserActive(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleUserActive(id));
    }
}