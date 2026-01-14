package com.viniciuskegler.salesapp.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Set;

public record ProductDetailsDTO(
        Long id,

        @NotNull
        @NotBlank
        @Length(max = 50)
        String title,

        @NotNull
        @NotBlank
        @Length(max = 255)
        String description,

        @NotNull
        @PositiveOrZero
        @Digits(integer = 8, fraction = 2)
        BigDecimal price,

        @PositiveOrZero
        @Digits(integer = 3, fraction = 2)
        BigDecimal discountPercentage,

        @PositiveOrZero
        @Digits(integer = 1, fraction = 2)
        BigDecimal rating,

        @PositiveOrZero
        Integer stock,

        @NotNull
        @NotBlank
        @Length(max = 20)
        String brand,

        @NotNull
        @NotBlank
        @Length(max = 15)
        String sku,

        Set<ReviewDTO> reviews,

        @NotNull
        @NotBlank
        String thumbnail) {
}
