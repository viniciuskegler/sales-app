package com.viniciuskegler.salesapp.mapper;

import com.viniciuskegler.salesapp.customer.Customer;
import com.viniciuskegler.salesapp.product.dto.ProductDTO;
import com.viniciuskegler.salesapp.product.dto.ProductDetailsDTO;
import com.viniciuskegler.salesapp.product.dto.ReviewDTO;
import com.viniciuskegler.salesapp.product.dto.mapper.ProductMapper;
import com.viniciuskegler.salesapp.product.Product;
import com.viniciuskegler.salesapp.product.ProductImage;
import com.viniciuskegler.salesapp.product.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
    }

    @Test
    void toProductDTO_mapsBasicFields() {
        Product product = new Product();
        product.setId(42L);
        product.setTitle("Test Product");
        product.setPrice(BigDecimal.valueOf(19.95));
        product.setThumbnail("thumb.png");

        ProductDTO dto = mapper.toProductDTO(product);

        assertNotNull(dto);
        assertEquals(42L, dto.id());
        assertEquals("Test Product", dto.title());
        assertEquals(BigDecimal.valueOf(19.95), dto.price());
        assertEquals("thumb.png", dto.thumbnail());
    }

    @Test
    void toReviewDTO_mapsFields() {
        Customer customer = new Customer();
        customer.setId(10L);
        customer.setFirstName("Alice");
        customer.setLastName("Smith");

        Review review = new Review();
        review.setId(7L);
        review.setComment("Great!");
        review.setRating(4);
        review.setCustomer(customer);
        LocalDateTime date = LocalDateTime.now();
        review.setReviewDate(date);

        ReviewDTO dto = mapper.toReviewDTO(review);

        assertNotNull(dto);
        assertEquals(7L, dto.id());
        assertEquals(10L, dto.reviewerId());
        assertEquals("Alice Smith", dto.reviewerName());
        assertEquals("Great!", dto.comment());
        assertEquals(4, dto.rating());
        assertEquals(date, dto.reviewDate());
    }

    @Test
    void toProductDetailsDTO_mapsAllFields_includingReviews() {
        LocalDateTime now = LocalDateTime.now();

        Customer customer = new Customer();
        customer.setId(20L);
        customer.setFirstName("Bob");
        customer.setLastName("Jones");

        Review review = new Review();
        review.setId(1L);
        review.setComment("Nice");
        review.setRating(5);
        review.setReviewDate(now);
        review.setCustomer(customer);

        Product product = new Product();
        product.setId(100L);
        product.setTitle("Detailed Product");
        product.setDescription("Full description");
        product.setPrice(BigDecimal.valueOf(49.99));
        product.setDiscountPercentage(BigDecimal.valueOf(10.0));
        product.setRating(BigDecimal.valueOf(5.0));
        product.setStock(20);
        product.setBrand("BrandX");
        product.setSku("SKU-123");
        product.setThumbnail("detail.png");
        product.setReviews(List.of(review));

        ProductImage img1 = new ProductImage();
        img1.setUrl("img-a.png");
        img1.setPosition(1);
        ProductImage img2 = new ProductImage();
        img2.setUrl("img-b.png");
        img2.setPosition(2);
        product.setImages(List.of(img1, img2));

        ProductDetailsDTO dto = mapper.toProductDetailsDTO(product);

        assertNotNull(dto);
        assertEquals(100L, dto.id());
        assertEquals("Detailed Product", dto.title());
        assertEquals("Full description", dto.description());
        assertEquals(BigDecimal.valueOf(49.99), dto.price());
        assertEquals(BigDecimal.valueOf(10.0), dto.discountPercentage());
        assertEquals(BigDecimal.valueOf(5.0), dto.rating());
        assertEquals(20, dto.stock());
        assertEquals("BrandX", dto.brand());
        assertEquals("SKU-123", dto.sku());
        assertEquals("detail.png", dto.thumbnail());

        assertNotNull(dto.reviews());
        assertEquals(1, dto.reviews().size());

        ReviewDTO mapped = dto.reviews().getFirst();
        assertEquals(1L, mapped.id());
        assertEquals(20L, mapped.reviewerId());
        assertEquals("Bob Jones", mapped.reviewerName());
        assertEquals("Nice", mapped.comment());
        assertEquals(5, mapped.rating());
        assertEquals(now, mapped.reviewDate());

        assertNotNull(dto.images());
        assertEquals(2, dto.images().size());
        assertEquals("img-a.png", dto.images().get(0));
        assertEquals("img-b.png", dto.images().get(1));
    }

    @Test
    void toProductDetailsDTO_withNoImages_returnsEmptyList() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("No Images");
        product.setDescription("desc");
        product.setPrice(BigDecimal.ONE);
        product.setDiscountPercentage(BigDecimal.ZERO);
        product.setRating(BigDecimal.ZERO);
        product.setStock(0);
        product.setBrand("B");
        product.setSku("S");
        product.setThumbnail("t.png");
        product.setReviews(List.of());
        product.setImages(List.of());

        ProductDetailsDTO dto = mapper.toProductDetailsDTO(product);

        assertNotNull(dto.images());
        assertTrue(dto.images().isEmpty());
    }
}
