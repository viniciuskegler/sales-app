package com.viniciuskegler.salesapp.product;

import com.viniciuskegler.salesapp.product.dto.ProductCategoryDTO;
import com.viniciuskegler.salesapp.product.dto.ProductDTO;
import com.viniciuskegler.salesapp.product.dto.ProductDetailsDTO;
import com.viniciuskegler.salesapp.shared.enums.SortOrder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
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

    @GetMapping("/{id}/related")
    public List<ProductDTO> getRelatedProducts(@PathVariable Long id) {
        return productService.getRelatedProducts(id);
    }

    @GetMapping("/categories")
    public List<ProductCategoryDTO> getAllProductsCategories() {
        return productService.getAllProductsCategories();
    }
}
