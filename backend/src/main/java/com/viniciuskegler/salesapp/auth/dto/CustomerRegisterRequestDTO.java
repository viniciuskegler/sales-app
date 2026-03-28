package com.viniciuskegler.salesapp.auth.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerRegisterRequestDTO extends BaseAuthDTO {
    @NotBlank
    @Nonnull
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Nonnull
    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Nonnull
    @Size(max = 20)
    private String phoneNumber;
}
