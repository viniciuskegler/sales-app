package com.viniciuskegler.salesapp.order;

import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderStatusAdvancerService {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusAdvancerService.class);

    private final OrderRepository orderRepository;
    private final OrderStatusPublisher statusPublisher;

    public OrderStatusAdvancerService(OrderRepository orderRepository, OrderStatusPublisher statusPublisher) {
        this.orderRepository = orderRepository;
        this.statusPublisher = statusPublisher;
    }

    @Transactional
    public int advanceStatuses() {
        List<Order> orders = orderRepository.findByStatusIn(
                List.of(OrderStatus.CONFIRMED, OrderStatus.SHIPPED)
        );

        for (Order order : orders) {
            OrderStatus next = switch (order.getStatus()) {
                case CONFIRMED -> OrderStatus.SHIPPED;
                case SHIPPED -> OrderStatus.DELIVERED;
                default -> throw new IllegalStateException("Unexpected status: " + order.getStatus());
            };
            order.setStatus(next);
            statusPublisher.publish(order, next);
        }

        log.info("Advanced {} order(s) to next status", orders.size());
        return orders.size();
    }
}
