package com.foodie.service;

import com.foodie.dto.response.AdminDashboardSummaryResponse;
import com.foodie.dto.response.UserSummaryResponse;
import com.foodie.entity.Order;
import com.foodie.entity.OrderStatus;
import com.foodie.entity.Restaurant;
import com.foodie.entity.User;
import com.foodie.exception.ResourceNotFoundException;
import com.foodie.mapper.EntityDtoMapper;
import com.foodie.repository.CouponRepository;
import com.foodie.repository.OrderRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final EntityDtoMapper mapper;

    public AdminService(UserRepository userRepository,
                        RestaurantRepository restaurantRepository,
                        OrderRepository orderRepository,
                        CouponRepository couponRepository,
                        EntityDtoMapper mapper) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.couponRepository = couponRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public AdminDashboardSummaryResponse getDashboardSummary() {
        AdminDashboardSummaryResponse summary = new AdminDashboardSummaryResponse();

        List<Order> orders = orderRepository.findAll();
        double gmv = orders.stream()
                .filter(o -> o.getOrderStatus() != OrderStatus.CANCELLED)
                .mapToDouble(Order::getGrandTotal)
                .sum();
        long activeCount = orders.stream()
                .filter(o -> o.getOrderStatus() != OrderStatus.DELIVERED && o.getOrderStatus() != OrderStatus.CANCELLED)
                .count();

        summary.setTotalGmv(Math.round(gmv * 100.0) / 100.0);
        summary.setActiveOrders(activeCount);
        summary.setTotalOrders((long) orders.size());
        summary.setTotalUsers(userRepository.count());
        summary.setActiveRestaurants(restaurantRepository.count());
        summary.setActiveCoupons((long) couponRepository.findByActiveTrue().size());

        return summary;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toUserSummary)
                .collect(Collectors.toList());
    }

    public UserSummaryResponse toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setActive(!user.isActive());
        user = userRepository.save(user);
        return mapper.toUserSummary(user);
    }
}