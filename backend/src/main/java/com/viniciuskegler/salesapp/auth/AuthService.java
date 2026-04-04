package com.viniciuskegler.salesapp.auth;

import com.viniciuskegler.salesapp.auth.contracts.AuthStrategyRegistry;
import com.viniciuskegler.salesapp.auth.dto.BaseAuthDTO;
import com.viniciuskegler.salesapp.auth.dto.BaseAuthResponseDTO;
import com.viniciuskegler.salesapp.user.UserRepository;
import com.viniciuskegler.salesapp.user.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthStrategyRegistry strategyRegistry;

    public AuthService(UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       AuthStrategyRegistry strategyRegistry) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.strategyRegistry = strategyRegistry;
    }

    public BaseAuthResponseDTO<?> login(BaseAuthDTO login) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
        );

        User user = userRepository.findByEmail(login.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login.getEmail()));

        return strategyRegistry.getFor(user.getRole()).buildResponse(user, "Login successful");
    }
}
