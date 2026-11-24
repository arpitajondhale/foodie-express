package com.foodie.controller;

import com.foodie.dto.request.CouponValidateRequest;
import com.foodie.dto.response.CouponResponse;
import com.foodie.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@Tag(name = "Coupons & Discounts", description = "Endpoints for discovering active promo codes and validating cart discounts.")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    @Operation(summary = "Get list of all active coupons with discounts and minimum order limits")
    public ResponseEntity<List<CouponResponse>> getActiveCoupons() {
        return ResponseEntity.ok(couponService.getActiveCoupons());
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate promo code against cart subtotal and calculate discount amount")
    public ResponseEntity<Map<String, Object>> validateCoupon(@Valid @RequestBody CouponValidateRequest request) {
        return ResponseEntity.ok(couponService.validateAndApplyCoupon(request.getCode(), request.getSubtotal()));
    }
}