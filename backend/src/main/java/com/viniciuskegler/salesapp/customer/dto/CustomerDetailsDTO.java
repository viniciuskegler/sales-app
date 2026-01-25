package com.viniciuskegler.salesapp.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record CustomerDetailsDTO(
        @Positive
        Long id,

        @NotNull
        @NotBlank
        @Length(max = 101)
        String fullName,

        @Length(max = 50)
        String email,

        @NotNull
        @NotBlank
        @Length(max = 20)
        String phone) {
}