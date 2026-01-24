package com.viniciuskegler.salesapp;

import com.viniciuskegler.salesapp.product.dto.ProductCategoryDTO;
import com.viniciuskegler.salesapp.product.dto.ProductDTO;
import com.viniciuskegler.salesapp.product.dto.ProductDetailsDTO;
import com.viniciuskegler.salesapp.shared.RestResponsePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductsApiIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16");

    @Autowired
    private WebApplicationContext context;

    private RestTestClient restTestClient;

    @BeforeEach
    void beforeEach() {
        restTestClient = RestTestClient.bindTo(webAppContextSetup(context).build())
                .defaultHeaders(headers -> headers.setBasicAuth("user", "password")).build();
    }


    @Test
    void shouldReturnPagedProducts() {
        RestResponsePage<ProductDTO> products = restTestClient.get()
                .uri("/api/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ProductDTO>>() {})
                .returnResult().getResponseBody();

        assertThat(products).isNotNull();
        assertThat(products.getContent()).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
            "/api/products?categories=electronics",
            "/api/products?title=phone"
    })
    void shouldFilterProducts(String uri) {
        RestResponsePage<ProductDTO> products = restTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ProductDTO>>() {})
                .returnResult().getResponseBody();

        assertThat(products).isNotNull();
    }

    @Test
    void shouldApplyPagination() {
        RestResponsePage<ProductDTO> products = restTestClient.get()
                .uri("/api/products?page=0&pageSize=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ProductDTO>>() {})
                .returnResult().getResponseBody();

        assertThat(products).isNotNull();
        assertThat(products.getSize()).isEqualTo(5);
    }

    @Test
    void shouldSortProductsDescending() {
        RestResponsePage<ProductDTO> products = restTestClient.get()
                .uri("/api/products?sortBy=price&sortOrder=DESC")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<RestResponsePage<ProductDTO>>() {})
                .returnResult().getResponseBody();

        assertThat(products).isNotNull();
    }

    @Test
    void shouldReturnProductDetailsById() {
        ProductDetailsDTO product = restTestClient.get()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDetailsDTO.class)
                .returnResult().getResponseBody();

        assertThat(product).isNotNull();
        assertThat(product.id()).isEqualTo(1L);
    }

    @Test()
    void shouldReturnNotFoundForInvalidProductId() {
        restTestClient.get()
                .uri("/api/products/999999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturnAllCategories() {
        List<ProductCategoryDTO> categories = restTestClient.get()
                .uri("/api/products/categories")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ProductCategoryDTO>>() {})
                .returnResult().getResponseBody();

        assertThat(categories).isNotNull();
    }

    @Test
    void shouldReturnBadRequestForInvalidSortOrder() {
        restTestClient.get()
                .uri("/api/products?sortOrder=INVALID")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnBadRequestWhenPageSizeExceedsMax() {
        restTestClient.get()
                .uri("/api/products?pageSize=101")
                .exchange()
                .expectStatus().isBadRequest();
    }

}
