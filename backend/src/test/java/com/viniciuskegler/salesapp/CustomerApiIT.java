package com.viniciuskegler.salesapp;

import com.viniciuskegler.salesapp.auth.dto.BaseAuthResponseDTO;
import com.viniciuskegler.salesapp.auth.dto.CustomerAuthResponseDTO;
import com.viniciuskegler.salesapp.auth.dto.CustomerRegisterRequestDTO;
import com.viniciuskegler.salesapp.customer.dto.CustomerDetailsDTO;
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
class CustomerApiIT {

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
    void shouldReturnCustomerDetailsWithValidToken() {
        BaseAuthResponseDTO<CustomerAuthResponseDTO> registered = registerCustomer(uniqueRegisterRequest());
        String token = registered.getToken();
        Long customerId = registered.getUserDetails().getId();

        CustomerDetailsDTO customer = restTestClient.get()
                .uri("/api/customers/{id}", customerId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDetailsDTO.class)
                .returnResult().getResponseBody();

        assertThat(customer).isNotNull();
        assertThat(customer.id()).isEqualTo(customerId);
        assertThat(customer.fullName()).isNotBlank();
        assertThat(customer.email()).isNotBlank();
        assertThat(customer.phone()).isNotBlank();
    }

    @Test
    void shouldReturn404ForUnknownCustomerId() {
        BaseAuthResponseDTO<CustomerAuthResponseDTO> registered = registerCustomer(uniqueRegisterRequest());
        String token = registered.getToken();

        restTestClient.get()
                .uri("/api/customers/999999")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn403WithoutToken() {
        restTestClient.get()
                .uri("/api/customers/1")
                .exchange()
                .expectStatus().isForbidden();
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
        CustomerRegisterRequestDTO request = new CustomerRegisterRequestDTO("Jane", "Doe", phone);
        request.setEmail("cust_" + unique + "@example.com");
        request.setPassword("password123");
        return request;
    }
}
