package com.foodie.controller;

import com.foodie.dto.request.ReviewCreateRequest;
import com.foodie.dto.response.ReviewResponse;
import com.foodie.entity.User;
import com.foodie.service.AuthService;
import com.foodie.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Ratings & Customer Reviews", description = "Endpoints for rating delivered meals and exploring customer feedback.")
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService authService;

    public ReviewController(ReviewService reviewService, AuthService authService) {
        this.reviewService = reviewService;
        this.authService = authService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get list of customer reviews and ratings for a restaurant")
    public ResponseEntity<List<ReviewResponse>> getRestaurantReviews(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(reviewService.getRestaurantReviews(restaurantId));
    }

    @PostMapping
    @Operation(summary = "Submit a post-delivery meal rating and review")
    public ResponseEntity<ReviewResponse> addReview(@Valid @RequestBody ReviewCreateRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(user, request));
    }
}