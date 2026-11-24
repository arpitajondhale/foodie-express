package com.foodie.websocket;

import com.foodie.dto.response.OrderResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public OrderWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastOrderStatus(OrderResponse order) {
        // Send to general order topic
        messagingTemplate.convertAndSend("/topic/orders/" + order.getId(), order);
        messagingTemplate.convertAndSend("/topic/orders/" + order.getOrderNumber(), order);

        // Send to restaurant kitchen topic
        if (order.getRestaurantId() != null) {
            messagingTemplate.convertAndSend("/topic/restaurant/" + order.getRestaurantId() + "/orders", order);
        }

        // Send to delivery fleet topic
        messagingTemplate.convertAndSend("/topic/delivery/orders", order);
    }

    public void broadcastDeliveryLocation(Long orderId, Double latitude, Double longitude, String status) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("latitude", latitude);
        payload.put("longitude", longitude);
        payload.put("status", status);
        payload.put("timestamp", System.currentTimeMillis());

        messagingTemplate.convertAndSend("/topic/orders/" + orderId + "/location", payload);
    }
}