package com.viniciuskegler.salesapp;

import com.viniciuskegler.salesapp.auth.dto.BaseAuthResponseDTO;
import com.viniciuskegler.salesapp.customer.dto.CustomerAuthResponseDTO;
import com.viniciuskegler.salesapp.customer.dto.CustomerRegisterRequestDTO;
import com.viniciuskegler.salesapp.order.dto.OrderDTO;
import com.viniciuskegler.salesapp.order.dto.OrderItemRequestDTO;
import com.viniciuskegler.salesapp.order.dto.PlaceOrderRequestDTO;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderApiIT {

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
    void shouldPlaceOrderAndReturn201WithDetails() {
        String token = registerAndGetToken();

        OrderDTO order = placeOrder(token, new PlaceOrderRequestDTO(List.of(
                new OrderItemRequestDTO(1L, 2)
        )));

        assertThat(order).isNotNull();
        assertThat(order.id()).isPositive();
        assertThat(order.status()).isEqualTo("PENDING");
        assertThat(order.total()).isPositive();
        assertThat(order.createdAt()).isNotNull();
        assertThat(order.items()).hasSize(1);
        assertThat(order.items().getFirst().productId()).isEqualTo(1L);
        assertThat(order.items().getFirst().quantity()).isEqualTo(2);
        assertThat(order.items().getFirst().unitPrice()).isPositive();
        assertThat(order.items().getFirst().totalPrice()).isPositive();
    }

    @Test
    void shouldCalculateTotalCorrectlyForMultipleItems() {
        String token = registerAndGetToken();

        OrderDTO order = placeOrder(token, new PlaceOrderRequestDTO(List.of(
                new OrderItemRequestDTO(1L, 1),
                new OrderItemRequestDTO(2L, 3)
        )));

        assertThat(order.items()).hasSize(2);

        var expectedTotal = order.items().stream()
                .map(i -> i.totalPrice())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        assertThat(order.total()).isEqualByComparingTo(expectedTotal);
    }

    @Test
    void shouldListOwnOrdersAfterPlacing() {
        String token = registerAndGetToken();

        placeOrder(token, new PlaceOrderRequestDTO(List.of(new OrderItemRequestDTO(1L, 1))));

        List<OrderDTO> orders = restTestClient.get()
                .uri("/api/orders")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<OrderDTO>>() {})
                .returnResult().getResponseBody();

        assertThat(orders).isNotNull();
        assertThat(orders).isNotEmpty();
        orders.forEach(o -> {
            assertThat(o.id()).isPositive();
            assertThat(o.status()).isNotBlank();
            assertThat(o.total()).isNotNull();
            assertThat(o.items()).isNotEmpty();
        });
    }

    @Test
    void shouldReturnOrderByIdForOwner() {
        String token = registerAndGetToken();
        OrderDTO placed = placeOrder(token, new PlaceOrderRequestDTO(List.of(new OrderItemRequestDTO(1L, 1))));

        OrderDTO fetched = restTestClient.get()
                .uri("/api/orders/" + placed.id())
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDTO.class)
                .returnResult().getResponseBody();

        assertThat(fetched).isNotNull();
        assertThat(fetched.id()).isEqualTo(placed.id());
        assertThat(fetched.status()).isEqualTo("PENDING");
    }

    @Test
    void shouldReturn404WhenFetchingAnotherUsersOrder() {
        String tokenA = registerAndGetToken();
        String tokenB = registerAndGetToken();

        OrderDTO orderA = placeOrder(tokenA, new PlaceOrderRequestDTO(List.of(new OrderItemRequestDTO(1L, 1))));

        restTestClient.get()
                .uri("/api/orders/" + orderA.id())
                .header("Authorization", "Bearer " + tokenB)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotIncludeOtherUsersOrdersInList() {
        String tokenA = registerAndGetToken();
        String tokenB = registerAndGetToken();

        placeOrder(tokenA, new PlaceOrderRequestDTO(List.of(new OrderItemRequestDTO(1L, 1))));

        List<OrderDTO> ordersB = restTestClient.get()
                .uri("/api/orders")
                .header("Authorization", "Bearer " + tokenB)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<OrderDTO>>() {})
                .returnResult().getResponseBody();

        assertThat(ordersB).isNotNull().isEmpty();
    }

    @Test
    void shouldReturn403WhenPlacingOrderWithoutToken() {
        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO(List.of(new OrderItemRequestDTO(1L, 1)));

        restTestClient.post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldReturn400WhenItemsListIsEmpty() {
        String token = registerAndGetToken();

        restTestClient.post()
                .uri("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new PlaceOrderRequestDTO(List.of()))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() {
        String token = registerAndGetToken();

        restTestClient.post()
                .uri("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new PlaceOrderRequestDTO(List.of(new OrderItemRequestDTO(999999L, 1))))
                .exchange()
                .expectStatus().isNotFound();
    }

    private OrderDTO placeOrder(String token, PlaceOrderRequestDTO request) {
        return restTestClient.post()
                .uri("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderDTO.class)
                .returnResult().getResponseBody();
    }

    private String registerAndGetToken() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String phone = "555" + unique.replaceAll("[^0-9]", "0").substring(0, 7);
        CustomerRegisterRequestDTO request = new CustomerRegisterRequestDTO("Jane", "Doe", phone);
        request.setEmail("order_" + unique + "@example.com");
        request.setPassword("password123");

        BaseAuthResponseDTO<CustomerAuthResponseDTO> response = restTestClient.post()
                .uri("/api/auth/register-customer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseAuthResponseDTO<CustomerAuthResponseDTO>>() {})
                .returnResult().getResponseBody();

        return response.getToken();
    }
}
