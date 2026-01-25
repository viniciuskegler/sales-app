package com.viniciuskegler.salesapp.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record ReviewDTO(
        @Positive
        Long id,

        @Positive
        Long reviewerId,

        @NotNull
        @NotBlank
        @Length(max = 100)
        String reviewerName,

        @NotNull
        @NotBlank
        @Length(max = 255)
        String comment,

        @NotNull
        @PositiveOrZero
        Integer rating,

        @NotNull
        LocalDateTime reviewDate) {
}
