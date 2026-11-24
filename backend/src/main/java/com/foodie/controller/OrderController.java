package com.foodie.controller;

import com.foodie.dto.request.OrderCreateRequest;
import com.foodie.dto.request.OrderStatusUpdateRequest;
import com.foodie.dto.response.OrderResponse;
import com.foodie.entity.User;
import com.foodie.service.AuthService;
import com.foodie.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders & Fulfillment", description = "Endpoints for placing orders, kitchen queue processing, and live lifecycle updates.")
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;

    public OrderController(OrderService orderService, AuthService authService) {
        this.orderService = orderService;
        this.authService = authService;
    }

    @PostMapping
    @Operation(summary = "Place a new food delivery order with optional customizations and coupons")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request, user));
    }

    @GetMapping
    @Operation(summary = "Get order history for the currently authenticated customer")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(orderService.getCustomerOrders(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single order details by ID")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(orderService.getOrderById(id, user));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Track order details by public Order Code (e.g. FE-20261124-4821)")
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber, user));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    @Operation(summary = "Get real-time incoming and active kitchen orders for a restaurant")
    public ResponseEntity<List<OrderResponse>> getRestaurantOrders(@PathVariable Long restaurantId) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(orderService.getRestaurantOrders(restaurantId, user));
    }

    @GetMapping("/delivery-queue")
    @PreAuthorize("hasAnyAuthority('ROLE_DELIVERY_PARTNER', 'ROLE_ADMIN')")
    @Operation(summary = "Get delivery partner queue of orders awaiting pickup or in transit")
    public ResponseEntity<List<OrderResponse>> getDeliveryQueue() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(orderService.getDeliveryQueue(user));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_DELIVERY_PARTNER', 'ROLE_ADMIN')")
    @Operation(summary = "Advance order lifecycle state (CONFIRMED, PREPARING, READY_FOR_PICKUP, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request, user));
    }

    @PatchMapping("/{id}/assign-rider")
    @PreAuthorize("hasAnyAuthority('ROLE_DELIVERY_PARTNER', 'ROLE_ADMIN')")
    @Operation(summary = "Accept/Assign order pickup to the authenticated delivery partner")
    public ResponseEntity<OrderResponse> assignRider(@PathVariable Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(orderService.assignDeliveryPartner(id, user));
    }
}