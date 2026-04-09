package com.viniciuskegler.salesapp.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDTO(

        @NotNull
        @Positive
        Long productId,

        @NotNull
        @Positive
        Integer quantity

) {}
