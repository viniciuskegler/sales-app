package com.viniciuskegler.salesapp.auth;

import com.viniciuskegler.salesapp.auth.dto.BaseAuthDTO;
import com.viniciuskegler.salesapp.auth.dto.BaseAuthResponseDTO;
import com.viniciuskegler.salesapp.customer.dto.CustomerRegisterRequestDTO;
import com.viniciuskegler.salesapp.customer.CustomerRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final CustomerRegistrationService customerRegistrationService;

    public AuthController(AuthService authService, CustomerRegistrationService customerRegistrationService) {
        this.authService = authService;
        this.customerRegistrationService = customerRegistrationService;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseAuthResponseDTO<?>> login(@RequestBody BaseAuthDTO baseAuthDTO) {
        return new ResponseEntity<>(authService.login(baseAuthDTO), HttpStatus.OK);
    }

    @PostMapping("/register-customer")
    public ResponseEntity<BaseAuthResponseDTO<?>> register(@Valid @RequestBody CustomerRegisterRequestDTO request) {
        return new ResponseEntity<>(customerRegistrationService.register(request), HttpStatus.OK);
    }
}
