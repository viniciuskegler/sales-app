package com.viniciuskegler.salesapp.auth;

import com.viniciuskegler.salesapp.auth.config.JwtGeneratorService;
import io.jsonwebtoken.ExpiredJwtException;
import com.viniciuskegler.salesapp.user.model.User;
import com.viniciuskegler.salesapp.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtGeneratorServiceTest {

    private JwtGeneratorService jwtGeneratorService;

    private User testUser;

    @BeforeEach
    void setUp() {
        jwtGeneratorService = new JwtGeneratorService();
        ReflectionTestUtils.setField(jwtGeneratorService, "jwtSecret",
                "test-secret-key-for-unit-testing-purposes-only-must-be-long-enough");
        ReflectionTestUtils.setField(jwtGeneratorService, "jwtExpirationMs", 86_400_000);
        jwtGeneratorService.init();

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("hashed")
                .role(UserRole.CUSTOMER)
                .build();
    }

    @Test
    void shouldGenerateNonBlankToken() {
        String token = jwtGeneratorService.generateToken(testUser);

        assertThat(token).isNotBlank();
    }

    @Test
    void shouldExtractCorrectUsernameFromToken() {
        String token = jwtGeneratorService.generateToken(testUser);

        assertThat(jwtGeneratorService.extractUsername(token)).isEqualTo(testUser.getEmail());
    }

    @Test
    void shouldValidateTokenForCorrectUser() {
        String token = jwtGeneratorService.generateToken(testUser);

        assertThat(jwtGeneratorService.isTokenValid(token, testUser)).isTrue();
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        String token = jwtGeneratorService.generateToken(testUser);

        User otherUser = User.builder()
                .id(2L)
                .email("other@example.com")
                .password("hashed")
                .role(UserRole.CUSTOMER)
                .build();

        assertThat(jwtGeneratorService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void shouldRejectTamperedToken() {
        String token = jwtGeneratorService.generateToken(testUser);
        String tampered = token + "tampered";

        assertThatThrownBy(() -> jwtGeneratorService.isTokenValid(tampered, testUser))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldRejectExpiredToken() {
        ReflectionTestUtils.setField(jwtGeneratorService, "jwtExpirationMs", -1000);
        jwtGeneratorService.init();

        String token = jwtGeneratorService.generateToken(testUser);

        assertThatThrownBy(() -> jwtGeneratorService.isTokenValid(token, testUser))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void shouldIncludeUserIdAndRoleInPayload() {
        String token = jwtGeneratorService.generateToken(testUser);

        String id = jwtGeneratorService.extractClaim(token, claims -> claims.get("id", String.class));
        String role = jwtGeneratorService.extractClaim(token, claims -> claims.get("role", String.class));

        assertThat(id).isEqualTo(testUser.getId().toString());
        assertThat(role).isEqualTo(UserRole.CUSTOMER.name());
    }
}
