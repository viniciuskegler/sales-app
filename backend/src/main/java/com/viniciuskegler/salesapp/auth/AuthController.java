package com.viniciuskegler.salesapp.auth;

import com.viniciuskegler.salesapp.auth.dto.BaseAuthDTO;
import com.viniciuskegler.salesapp.auth.dto.BaseAuthResponseDTO;
import com.viniciuskegler.salesapp.auth.dto.CustomerAuthResponseDTO;
import com.viniciuskegler.salesapp.auth.dto.CustomerRegisterRequestDTO;
import com.viniciuskegler.salesapp.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseAuthResponseDTO<CustomerAuthResponseDTO>> login(@RequestBody BaseAuthDTO baseAuthDTO) {
        return new ResponseEntity<>(userService.login(baseAuthDTO), HttpStatus.OK);
    }

    @PostMapping("/register-customer")
    public ResponseEntity<BaseAuthResponseDTO<CustomerAuthResponseDTO>> register(@Valid @RequestBody CustomerRegisterRequestDTO request) {
        BaseAuthResponseDTO<CustomerAuthResponseDTO> response = userService.registerCustomer(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
