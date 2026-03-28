package com.viniciuskegler.salesapp.auth.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseAuthDTO {
    @Email
    @Nonnull
    @Size(max = 50)
    private String email;

    @Nonnull
    @NotBlank
    private String password;
}
