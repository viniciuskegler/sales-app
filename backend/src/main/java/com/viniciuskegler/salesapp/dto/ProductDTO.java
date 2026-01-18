package com.viniciuskegler.salesapp.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record ProductDTO(
        @Positive
        Long id,

        @NotNull
        @NotBlank
        @Length(max = 50)
        String title,

        @NotNull
        @Positive
        @Digits(integer = 8, fraction = 2)
        BigDecimal price,

        @NotNull
        @NotBlank
        String thumbnail

) {
}
