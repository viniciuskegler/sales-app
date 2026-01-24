package com.viniciuskegler.salesapp.product.dto.mapper;

import com.viniciuskegler.salesapp.product.dto.ProductDTO;
import com.viniciuskegler.salesapp.product.dto.ProductDetailsDTO;
import com.viniciuskegler.salesapp.product.dto.ReviewDTO;
import com.viniciuskegler.salesapp.product.Product;
import com.viniciuskegler.salesapp.product.Review;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductDTO toProductDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getPrice(),
                product.getThumbnail()
        );
    }

    public ProductDetailsDTO toProductDetailsDTO(Product product) {
        Set<ReviewDTO> reviews = product.getReviews().stream()
                .map(this::toReviewDTO)
                .collect(Collectors.toSet());

        return new ProductDetailsDTO(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscountPercentage(),
                product.getRating(),
                product.getStock(),
                product.getBrand(),
                product.getSku(),
                reviews,
                product.getThumbnail()
        );
    }

    public ReviewDTO toReviewDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getComment(),
                review.getRating(),
                review.getReviewDate(),
                review.getReviewerName()
        );
    }
}
