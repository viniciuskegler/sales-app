package com.viniciuskegler.salesapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record ReviewDTO(
        @Positive
        Long id,

        @NotNull
        @NotBlank
        @Length(max = 255)
        String comment,

        @NotNull
        @PositiveOrZero
        Integer rating,

        @NotNull
        LocalDateTime reviewDate,

        @NotNull
        @NotBlank
        @Length(max = 80)
        String reviewerName) {
}
