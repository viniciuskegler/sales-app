package com.viniciuskegler.salesapp.controller;

import com.viniciuskegler.salesapp.dto.ProductCategoryDTO;
import com.viniciuskegler.salesapp.enums.SortOrder;
import com.viniciuskegler.salesapp.model.Product;
import com.viniciuskegler.salesapp.services.ProductService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<Product> listProducts(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Max(100) Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") SortOrder sortOrder
    ) {
        return productService.getProducts(page, pageSize, sortBy, sortOrder);
    }

    @GetMapping
    @RequestMapping("/{id}")
    public Product findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping
    @RequestMapping("/categories")
    public ProductCategoryDTO getCategories() {
        return productService.getCategories();
    }
}
