package com.viniciuskegler.salesapp.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(

        Long id,
        String status,
        BigDecimal total,
        LocalDateTime createdAt,
        List<OrderItemDTO> items

) {}
