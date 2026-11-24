package com.foodie.service;

import com.foodie.dto.request.OrderCreateRequest;
import com.foodie.dto.request.OrderItemRequest;
import com.foodie.dto.request.OrderStatusUpdateRequest;
import com.foodie.dto.response.OrderResponse;
import com.foodie.entity.*;
import com.foodie.exception.BadRequestException;
import com.foodie.exception.ResourceNotFoundException;
import com.foodie.exception.UnauthorizedException;
import com.foodie.mapper.EntityDtoMapper;
import com.foodie.repository.*;
import com.foodie.websocket.OrderWebSocketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final CustomizationOptionRepository optionRepository;
    private final AddressRepository addressRepository;
    private final CouponService couponService;
    private final OrderWebSocketService webSocketService;
    private final EntityDtoMapper mapper;

    public OrderService(OrderRepository orderRepository,
                        RestaurantRepository restaurantRepository,
                        FoodItemRepository foodItemRepository,
                        CustomizationOptionRepository optionRepository,
                        AddressRepository addressRepository,
                        CouponService couponService,
                        OrderWebSocketService webSocketService,
                        EntityDtoMapper mapper) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodItemRepository = foodItemRepository;
        this.optionRepository = optionRepository;
        this.addressRepository = addressRepository;
        this.couponService = couponService;
        this.webSocketService = webSocketService;
        this.mapper = mapper;
    }

    public OrderResponse createOrder(OrderCreateRequest request, User customer) {
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + request.getRestaurantId()));

        if (!restaurant.isOpen()) {
            throw new BadRequestException("Restaurant is currently closed and not accepting orders.");
        }

        // Resolve Delivery Address Snapshot
        String addressSnapshot = request.getDeliveryAddressSnapshot();
        if ((addressSnapshot == null || addressSnapshot.isBlank()) && request.getAddressId() != null) {
            Address address = addressRepository.findByIdAndUserId(request.getAddressId(), customer.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + request.getAddressId()));
            addressSnapshot = String.format("%s: %s, %s - %s", address.getLabel(), address.getStreet(), address.getCity(), address.getPincode());
        }
        if (addressSnapshot == null || addressSnapshot.isBlank()) {
            addressSnapshot = "Default: 124 5th Block, Koramangala, Bengaluru - 560034";
        }

        // Build Order Items & calculate itemTotal
        double itemTotal = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            FoodItem foodItem = foodItemRepository.findById(itemReq.getFoodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food item not found: " + itemReq.getFoodItemId()));

            if (!foodItem.isAvailable()) {
                throw new BadRequestException("Item is currently sold out: " + foodItem.getName());
            }

            double unitPrice = foodItem.getDiscountPrice() != null ? foodItem.getDiscountPrice() : foodItem.getPrice();
            List<String> customizationStrings = new ArrayList<>();

            if (itemReq.getSelectedOptionIds() != null && !itemReq.getSelectedOptionIds().isEmpty()) {
                for (Long optId : itemReq.getSelectedOptionIds()) {
                    CustomizationOption opt = optionRepository.findById(optId).orElse(null);
                    if (opt != null) {
                        unitPrice += opt.getExtraPrice();
                        customizationStrings.add(opt.getName() + " (+₹" + opt.getExtraPrice() + ")");
                    }
                }
            }

            double subtotal = unitPrice * itemReq.getQuantity();
            itemTotal += subtotal;

            OrderItem orderItem = new OrderItem();
            orderItem.setFoodItemId(foodItem.getId());
            orderItem.setFoodName(foodItem.getName());
            orderItem.setPrice(unitPrice);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setCustomizationDetails(String.join(", ", customizationStrings));
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
        }

        // Pricing breakdown
        double packagingFee = 25.0;
        double deliveryFee = itemTotal >= 500.0 ? 0.0 : 35.0;
        double gstAmount = Math.round(itemTotal * 0.05 * 100.0) / 100.0; // 5% GST
        double discountAmount = 0.0;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            try {
                Map<String, Object> couponResult = couponService.validateAndApplyCoupon(request.getCouponCode(), itemTotal);
                discountAmount = (Double) couponResult.get("discountAmount");
            } catch (Exception e) {
                // If coupon invalid, proceed with 0 discount
            }
        }

        double grandTotal = Math.max(0.0, itemTotal + packagingFee + deliveryFee + gstAmount - discountAmount);

        // Build Order
        Order order = new Order();
        String datePart = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        String orderNumber = String.format("FE-%s-%04d", datePart, ThreadLocalRandom.current().nextInt(1000, 9999));

        order.setOrderNumber(orderNumber);
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setDeliveryAddressSnapshot(addressSnapshot);
        order.setItemTotal(itemTotal);
        order.setPackagingFee(packagingFee);
        order.setDeliveryFee(deliveryFee);
        order.setGstAmount(gstAmount);
        order.setDiscountAmount(discountAmount);
        order.setGrandTotal(grandTotal);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(request.getPaymentMethod() == PaymentMethod.COD ? PaymentStatus.PENDING : PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setEstimatedDeliveryTime(String.format("%d-%d mins", restaurant.getAvgPrepTimeMins(), restaurant.getAvgPrepTimeMins() + 15));

        for (OrderItem oi : orderItems) {
            oi.setOrder(order);
        }
        order.setItems(orderItems);

        order = orderRepository.save(order);
        OrderResponse response = mapper.toOrderResponse(order);

        // WebSocket notification
        webSocketService.broadcastOrderStatus(response);

        return response;
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        validateOrderAccess(order, user);
        return mapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber, User user) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));

        validateOrderAccess(order, user);
        return mapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(User customer) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getRestaurantOrders(Long restaurantId, User user) {
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getDeliveryQueue(User partner) {
        return orderRepository.findByOrderStatusInOrderByCreatedAtDesc(
                List.of(OrderStatus.CONFIRMED, OrderStatus.PREPARING, OrderStatus.READY_FOR_PICKUP, OrderStatus.OUT_FOR_DELIVERY)
        ).stream().map(mapper::toOrderResponse).collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        order.setOrderStatus(request.getOrderStatus());
        if (request.getCancellationReason() != null) {
            order.setCancellationReason(request.getCancellationReason());
        }
        if (request.getOrderStatus() == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        order = orderRepository.save(order);
        OrderResponse res = mapper.toOrderResponse(order);
        webSocketService.broadcastOrderStatus(res);
        return res;
    }

    public OrderResponse assignDeliveryPartner(Long orderId, User partner) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        order.setDeliveryPartner(partner);
        if (order.getOrderStatus() == OrderStatus.READY_FOR_PICKUP) {
            order.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
        }
        order = orderRepository.save(order);
        OrderResponse res = mapper.toOrderResponse(order);
        webSocketService.broadcastOrderStatus(res);
        return res;
    }

    private void validateOrderAccess(Order order, User user) {
        if (user.getRole() == Role.ROLE_ADMIN) return;
        if (order.getCustomer().getId().equals(user.getId())) return;
        if (order.getRestaurant() != null && order.getRestaurant().getOwnerId() != null && order.getRestaurant().getOwnerId().equals(user.getId())) return;
        if (order.getDeliveryPartner() != null && order.getDeliveryPartner().getId().equals(user.getId())) return;
        if (user.getRole() == Role.ROLE_DELIVERY_PARTNER) return;

        throw new UnauthorizedException("You are not authorized to access this order.");
    }
}