package com.viniciuskegler.salesapp.auth.contracts;

import com.viniciuskegler.salesapp.auth.dto.BaseAuthResponseDTO;
import com.viniciuskegler.salesapp.user.dto.UserDTO;
import com.viniciuskegler.salesapp.user.enums.UserRole;
import com.viniciuskegler.salesapp.user.model.User;

public interface AuthResponseStrategy<T extends UserDTO> {
    UserRole support();
    BaseAuthResponseDTO<T> buildResponse(User user, String message);
}
