package com.viniciuskegler.salesapp.auth.dto;

import com.viniciuskegler.salesapp.user.dto.UserDTO;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseAuthResponseDTO<T extends UserDTO> {
    @Nonnull
    @NotBlank
    @Size(max = 50)
    private String message;

    @Nonnull
    @NotBlank
    private String token;

    private T userDetails;
}
