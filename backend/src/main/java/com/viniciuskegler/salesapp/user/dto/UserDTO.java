package com.viniciuskegler.salesapp.user.dto;

import com.viniciuskegler.salesapp.user.enums.UserRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserDTO {
    @Positive
    Long id;

    @NotNull
    @NotBlank
    @Max(50)
    String email;

    @NotNull
    @NotBlank
    UserRole role;
}
