package com.viniciuskegler.salesapp.order.dto.mapper;

import com.viniciuskegler.salesapp.order.dto.OrderDTO;
import com.viniciuskegler.salesapp.order.dto.OrderItemDTO;
import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderDTO toOrderDTO(Order order) {
        List<OrderItemDTO> items = order.getItems().stream()
                .map(this::toOrderItemDTO)
                .collect(java.util.stream.Collectors.toList());

        return new OrderDTO(
                order.getId(),
                order.getStatus().name(),
                order.getTotal(),
                order.getCreatedAt(),
                items
        );
    }

    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        return new OrderItemDTO(
                item.getProduct().getId(),
                item.getProduct().getTitle(),
                item.getProduct().getThumbnail(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }
}
