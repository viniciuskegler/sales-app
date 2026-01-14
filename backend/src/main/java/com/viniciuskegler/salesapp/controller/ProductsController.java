package com.viniciuskegler.salesapp.controller;

import com.viniciuskegler.salesapp.dto.ProductCategoryDTO;
import com.viniciuskegler.salesapp.dto.ProductDTO;
import com.viniciuskegler.salesapp.dto.ProductDetailsDTO;
import com.viniciuskegler.salesapp.enums.SortOrder;
import com.viniciuskegler.salesapp.services.ProductService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductDTO> listProducts(
            @RequestParam(required = false) @Size(max = 3) List<String> categories,
            @RequestParam(required = false) @Size(max = 50) String title,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Max(100) Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") SortOrder sortOrder
    ) {
        return productService.getProducts(categories, title, page, pageSize, sortBy, sortOrder);
    }

    @GetMapping("/{id}")
    public ProductDetailsDTO findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/categories")
    public List<ProductCategoryDTO> getAllProductsCategories() {
        return productService.getAllProductsCategories();
    }
}
