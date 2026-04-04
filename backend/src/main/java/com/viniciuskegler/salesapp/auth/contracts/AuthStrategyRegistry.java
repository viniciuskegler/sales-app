package com.viniciuskegler.salesapp.auth.contracts;

import com.viniciuskegler.salesapp.user.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AuthStrategyRegistry {

    private final Map<UserRole, AuthResponseStrategy<?>> strategies;

    public AuthStrategyRegistry(List<AuthResponseStrategy<?>> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(AuthResponseStrategy::support, s -> s));
    }

    public AuthResponseStrategy<?> getFor(UserRole role) {
        return Optional.ofNullable(strategies.get(role))
                .orElseThrow(() -> new IllegalArgumentException("No auth strategy registered for role: " + role));
    }
}
