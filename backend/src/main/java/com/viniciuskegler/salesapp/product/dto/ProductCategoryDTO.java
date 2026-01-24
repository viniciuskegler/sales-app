package com.viniciuskegler.salesapp.product.dto;

import jakarta.validation.constraints.NotNull;

public record ProductCategoryDTO(
        @NotNull String name,
        @NotNull String value
) {
}
