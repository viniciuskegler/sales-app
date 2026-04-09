package com.viniciuskegler.salesapp.order;

import com.viniciuskegler.salesapp.order.dto.OrderNotificationDTO;
import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderStatus;
import com.viniciuskegler.salesapp.payment.OrderStatusUpdateDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public OrderStatusPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(Order order, OrderStatus newStatus) {
        Long orderId = order.getId();
        Long userId = order.getCustomer().getUser().getId();

        messagingTemplate.convertAndSend(
                "/topic/orders/" + orderId,
                new OrderStatusUpdateDTO(newStatus.name())
        );

        messagingTemplate.convertAndSend(
                "/topic/users/" + userId + "/notifications",
                new OrderNotificationDTO(orderId, newStatus.name(), messageFor(newStatus))
        );
    }

    private String messageFor(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "Your payment was confirmed";
            case SHIPPED -> "Your order has been shipped";
            case DELIVERED -> "Your order has been delivered";
            case CANCELLED -> "Your payment was declined";
            default -> "Order status updated";
        };
    }
}