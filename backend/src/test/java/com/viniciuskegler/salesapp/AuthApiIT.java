package com.viniciuskegler.salesapp;

import com.viniciuskegler.salesapp.auth.dto.BaseAuthDTO;
import com.viniciuskegler.salesapp.auth.dto.BaseAuthResponseDTO;
import com.viniciuskegler.salesapp.customer.dto.CustomerAuthResponseDTO;
import com.viniciuskegler.salesapp.customer.dto.CustomerRegisterRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthApiIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer(DockerImageName.parse("postgres:16"));

    @Autowired
    private WebApplicationContext context;

    private RestTestClient restTestClient;

    @BeforeEach
    void beforeEach() {
        restTestClient = RestTestClient.bindTo(webAppContextSetup(context).apply(springSecurity()).build()).build();
    }

    @Test
    void shouldRegisterCustomerAndReturnTokenWithUserDetails() {
        BaseAuthResponseDTO<CustomerAuthResponseDTO> response = registerCustomer(uniqueRegisterRequest());

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUserDetails()).isNotNull();
        assertThat(response.getUserDetails().getEmail()).isNotNull();
        assertThat(response.getUserDetails().getFullName()).isNotBlank();
        assertThat(response.getUserDetails().getId()).isPositive();
    }

    @Test
    void shouldReturnErrorOnDuplicateEmail() {
        CustomerRegisterRequestDTO request = uniqueRegisterRequest();
        registerCustomer(request);

        restTestClient.post()
                .uri("/api/auth/register-customer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void shouldReturnBadRequestOnInvalidRegistrationPayload() {
        CustomerRegisterRequestDTO request = new CustomerRegisterRequestDTO("John", "Doe", "5551234567");
        request.setEmail("not-an-email");
        request.setPassword("pass");

        restTestClient.post()
                .uri("/api/auth/register-customer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldLoginAndReturnTokenWithUserDetails() {
        CustomerRegisterRequestDTO request = uniqueRegisterRequest();
        registerCustomer(request);

        BaseAuthResponseDTO<CustomerAuthResponseDTO> response = restTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BaseAuthDTO(request.getEmail(), request.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseAuthResponseDTO<CustomerAuthResponseDTO>>() {})
                .returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUserDetails().getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    void shouldReturnErrorOnWrongPassword() {
        CustomerRegisterRequestDTO request = uniqueRegisterRequest();
        registerCustomer(request);

        restTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BaseAuthDTO(request.getEmail(), "wrong-password"))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void shouldReturnErrorOnUnknownEmail() {
        restTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BaseAuthDTO("nobody@example.com", "password"))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void shouldDenyProtectedEndpointWithoutToken() {
        restTestClient.get()
                .uri("/api/customers/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldAllowProtectedEndpointWithValidToken() {
        BaseAuthResponseDTO<CustomerAuthResponseDTO> registered = registerCustomer(uniqueRegisterRequest());
        String token = registered.getToken();

        restTestClient.get()
                .uri("/api/customers/me")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldDenyProtectedEndpointWithTamperedToken() {
        BaseAuthResponseDTO<CustomerAuthResponseDTO> registered = registerCustomer(uniqueRegisterRequest());
        String tampered = registered.getToken() + "tampered";

        restTestClient.get()
                .uri("/api/customers/me")
                .header("Authorization", "Bearer " + tampered)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private BaseAuthResponseDTO<CustomerAuthResponseDTO> registerCustomer(CustomerRegisterRequestDTO request) {
        return restTestClient.post()
                .uri("/api/auth/register-customer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseAuthResponseDTO<CustomerAuthResponseDTO>>() {})
                .returnResult().getResponseBody();
    }

    private CustomerRegisterRequestDTO uniqueRegisterRequest() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String phone = "555" + unique.replaceAll("[^0-9]", "0").substring(0, 7);
        CustomerRegisterRequestDTO request = new CustomerRegisterRequestDTO("John", "Doe", phone);
        request.setEmail("user_" + unique + "@example.com");
        request.setPassword("password123");
        return request;
    }
}
