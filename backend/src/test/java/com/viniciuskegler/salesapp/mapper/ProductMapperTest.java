package com.viniciuskegler.salesapp.mapper;

import com.viniciuskegler.salesapp.dto.ProductDTO;
import com.viniciuskegler.salesapp.dto.ProductDetailsDTO;
import com.viniciuskegler.salesapp.dto.ReviewDTO;
import com.viniciuskegler.salesapp.dto.mapper.ProductMapper;
import com.viniciuskegler.salesapp.model.Product;
import com.viniciuskegler.salesapp.model.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductMapperTest {

    @Autowired
    private ProductMapper mapper;

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
        product.setPrice(BigDecimal.valueOf(19.95));
        assertEquals("thumb.png", dto.thumbnail());
    }

    @Test
    void toReviewDTO_mapsFields() {
        Review review = new Review();
        review.setId(7L);
        review.setComment("Great!");
        review.setRating(4);
        LocalDateTime date = LocalDateTime.now();
        review.setReviewDate(date);
        review.setReviewerName("Alice");

        ReviewDTO dto = mapper.toReviewDTO(review);

        assertNotNull(dto);
        assertEquals(7L, dto.id());
        assertEquals("Great!", dto.comment());
        assertEquals(4, dto.rating());
        assertEquals(date, dto.reviewDate());
        assertEquals("Alice", dto.reviewerName());
    }

    @Test
    void toProductDetailsDTO_mapsAllFields_includingReviews() {
        LocalDateTime now = LocalDateTime.now();
        Review review = new Review();
        review.setId(1L);
        review.setComment("Nice");
        review.setRating(5);
        review.setReviewDate(now);
        review.setReviewerName("Bob");

        Set<Review> reviews = new HashSet<>();
        reviews.add(review);

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
        product.setReviews(reviews);
        product.setThumbnail("detail.png");

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

        ReviewDTO mapped = dto.reviews().iterator().next();
        assertEquals(1L, mapped.id());
        assertEquals("Nice", mapped.comment());
        assertEquals(5, mapped.rating());
        assertEquals(now, mapped.reviewDate());
        assertEquals("Bob", mapped.reviewerName());
    }
}
