package com.viniciuskegler.salesapp.product.dto.mapper;

import com.viniciuskegler.salesapp.product.dto.ProductDTO;
import com.viniciuskegler.salesapp.product.dto.ProductDetailsDTO;
import com.viniciuskegler.salesapp.product.dto.ReviewDTO;
import com.viniciuskegler.salesapp.product.model.Product;
import com.viniciuskegler.salesapp.product.model.ProductImage;
import com.viniciuskegler.salesapp.product.model.Review;
import org.springframework.stereotype.Component;

import java.util.List;

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
        List<ReviewDTO> reviews = product.getReviews().stream()
                .map(this::toReviewDTO)
                .toList();

        List<String> images = product.getImages().stream()
                .map(ProductImage::getUrl)
                .toList();

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
                product.getThumbnail(),
                images
        );
    }

    public ReviewDTO toReviewDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getCustomer().getId(),
                review.getCustomer().getFullName(),
                review.getComment(),
                review.getRating(),
                review.getReviewDate()
        );
    }
}
