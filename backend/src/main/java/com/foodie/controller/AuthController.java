package com.foodie.controller;

import com.foodie.dto.request.LoginRequest;
import com.foodie.dto.request.RefreshTokenRequest;
import com.foodie.dto.request.SignupRequest;
import com.foodie.dto.response.AuthResponse;
import com.foodie.dto.response.UserSummaryResponse;
import com.foodie.entity.User;
import com.foodie.mapper.EntityDtoMapper;
import com.foodie.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication & Authorization", description = "Endpoints for user registration, login, token refresh, and profile inspection.")
public class AuthController {

    private final AuthService authService;
    private final EntityDtoMapper mapper;

    public AuthController(AuthService authService, EntityDtoMapper mapper) {
        this.authService = authService;
        this.mapper = mapper;
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user account with role")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and receive JWT access + refresh tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token and issue new 15-minute access token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated user's profile summary")
    public ResponseEntity<UserSummaryResponse> getMe() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(mapper.toUserSummary(user));
    }
}