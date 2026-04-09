package com.viniciuskegler.salesapp.order.dto;

import java.math.BigDecimal;

public record OrderItemDTO(

        Long productId,
        String title,
        String thumbnail,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice

) {}
