package com.foodie.service;

import com.foodie.dto.request.ReviewCreateRequest;
import com.foodie.dto.response.ReviewResponse;
import com.foodie.entity.Order;
import com.foodie.entity.Restaurant;
import com.foodie.entity.Review;
import com.foodie.entity.User;
import com.foodie.exception.ResourceNotFoundException;
import com.foodie.mapper.EntityDtoMapper;
import com.foodie.repository.OrderRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final EntityDtoMapper mapper;

    public ReviewService(ReviewRepository reviewRepository,
                         RestaurantRepository restaurantRepository,
                         OrderRepository orderRepository,
                         EntityDtoMapper mapper) {
        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getRestaurantReviews(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(mapper::toReviewResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponse addReview(User user, ReviewCreateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + request.getRestaurantId()));

        Order order = orderRepository.findById(request.getOrderId()).orElse(null);

        Review review = new Review();
        review.setUser(user);
        review.setRestaurant(restaurant);
        review.setOrder(order);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);

        // Update restaurant average rating
        List<Review> allReviews = reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurant.getId());
        double avg = allReviews.stream().mapToInt(Review::getRating).average().orElse(4.5);
        restaurant.setRating(Math.round(avg * 10.0) / 10.0);
        restaurant.setTotalRatings(allReviews.size());
        restaurantRepository.save(restaurant);

        return mapper.toReviewResponse(review);
    }
}