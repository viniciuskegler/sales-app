package com.viniciuskegler.salesapp.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PlaceOrderRequestDTO(

        @NotEmpty
        @Valid
        List<OrderItemRequestDTO> items

) {}
