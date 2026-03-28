package com.viniciuskegler.salesapp.auth.dto;

import com.viniciuskegler.salesapp.user.dto.UserDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerAuthResponseDTO extends UserDTO {
    private String fullName;

}

