package com.viniciuskegler.salesapp.product.specification;

import com.viniciuskegler.salesapp.product.model.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;


public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> hasCategory(List<String> categories) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(categories) || categories.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("category").in(categories);
        };
    }

    public static Specification<Product> hasTitleLike(String title) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isEmpty(title)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }
}
