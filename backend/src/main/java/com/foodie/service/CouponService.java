package com.foodie.service;

import com.foodie.dto.response.CouponResponse;
import com.foodie.entity.Coupon;
import com.foodie.entity.DiscountType;
import com.foodie.exception.BadRequestException;
import com.foodie.exception.ResourceNotFoundException;
import com.foodie.mapper.EntityDtoMapper;
import com.foodie.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;
    private final EntityDtoMapper mapper;

    public CouponService(CouponRepository couponRepository, EntityDtoMapper mapper) {
        this.couponRepository = couponRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CouponResponse> getActiveCoupons() {
        return couponRepository.findByActiveTrue()
                .stream()
                .map(mapper::toCouponResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> validateAndApplyCoupon(String code, Double subtotal) {
        if (code == null || code.isBlank()) {
            throw new BadRequestException("Coupon code is required");
        }

        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndActiveTrue(code.trim().toUpperCase())
                .orElseThrow(() -> new BadRequestException("Invalid or expired coupon code: " + code));

        if (coupon.getValidUntil() != null && coupon.getValidUntil().isBefore(Instant.now())) {
            throw new BadRequestException("Coupon has expired");
        }

        if (subtotal < coupon.getMinOrderAmount()) {
            throw new BadRequestException(String.format("Minimum order amount of ₹%.0f required to apply code %s", coupon.getMinOrderAmount(), code));
        }

        double discount = 0.0;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = (subtotal * coupon.getDiscountValue()) / 100.0;
            if (coupon.getMaxDiscount() != null && discount > coupon.getMaxDiscount()) {
                discount = coupon.getMaxDiscount();
            }
        } else {
            discount = Math.min(coupon.getDiscountValue(), subtotal);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("code", coupon.getCode());
        result.put("discountAmount", discount);
        result.put("coupon", mapper.toCouponResponse(coupon));
        return result;
    }
}