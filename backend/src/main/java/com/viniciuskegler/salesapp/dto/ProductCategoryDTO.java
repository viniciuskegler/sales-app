package com.viniciuskegler.salesapp.dto;

import jakarta.validation.constraints.NotNull;

public record ProductCategoryDTO(
        @NotNull String name,
        @NotNull String value
) {
}
