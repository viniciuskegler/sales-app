package com.viniciuskegler.salesapp.product;

import com.viniciuskegler.salesapp.product.dto.ProductCategoryDTO;
import com.viniciuskegler.salesapp.product.dto.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, productMapper);
    }

    @Test
    void getAllProductsCategories_formatsSimpleCategoryName() {
        when(productRepository.getAllProductsCategories()).thenReturn(List.of("beauty"));
        List<ProductCategoryDTO> result = productService.getAllProductsCategories();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Beauty");
        assertThat(result.getFirst().value()).isEqualTo("beauty");
    }

    @Test
    void getAllProductsCategories_replacesHyphensWithSpacesAndCapitalizes() {
        when(productRepository.getAllProductsCategories()).thenReturn(List.of("mens-shirts"));
        List<ProductCategoryDTO> result = productService.getAllProductsCategories();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Mens Shirts");
        assertThat(result.getFirst().value()).isEqualTo("mens-shirts");
    }

    @Test
    void getAllProductsCategories_handlesMultipleCategories() {
        when(productRepository.getAllProductsCategories())
                .thenReturn(List.of("beauty", "laptops", "mobile-accessories"));
        List<ProductCategoryDTO> result = productService.getAllProductsCategories();
        assertThat(result).hasSize(3);
        assertThat(result).extracting(ProductCategoryDTO::name)
                .containsExactly("Beauty", "Laptops", "Mobile Accessories");
        assertThat(result).extracting(ProductCategoryDTO::value)
                .containsExactly("beauty", "laptops", "mobile-accessories");
    }

    @Test
    void getAllProductsCategories_returnsEmptyListWhenNoCategories() {
        when(productRepository.getAllProductsCategories()).thenReturn(List.of());
        List<ProductCategoryDTO> result = productService.getAllProductsCategories();
        assertThat(result).isEmpty();
    }
}
