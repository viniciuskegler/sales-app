package com.viniciuskegler.salesapp.product;

import com.viniciuskegler.salesapp.product.dto.ProductCategoryDTO;
import com.viniciuskegler.salesapp.product.dto.ProductDTO;
import com.viniciuskegler.salesapp.product.dto.ProductDetailsDTO;
import com.viniciuskegler.salesapp.product.dto.mapper.ProductMapper;
import com.viniciuskegler.salesapp.shared.enums.SortOrder;
import com.viniciuskegler.salesapp.exception.RecordNotFoundException;
import com.viniciuskegler.salesapp.product.specification.ProductSpecification;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apache.commons.text.WordUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@Validated
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public Page<ProductDTO> getProducts(List<String> categories,
                                        String title,
                                        Integer page,
                                        Integer pageSize,
                                        String sortBy,
                                        SortOrder sortOrder) {
        Sort sort = Sort.by(sortBy);
        if (sortOrder.equals(SortOrder.ASC)) {
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }
        return productRepository.findAll(Specification.where(ProductSpecification.hasCategory(categories))
                        .and(ProductSpecification.hasTitleLike(title)), PageRequest.of(page, pageSize, sort))
                .map(productMapper::toProductDTO);
    }

    public ProductDetailsDTO findById(@NotNull @Positive Long id) {
        return productRepository.findById(id).map(productMapper::toProductDetailsDTO)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    public List<ProductCategoryDTO> getAllProductsCategories() {
        return productRepository.getAllProductsCategories().stream().map(cat ->
                new ProductCategoryDTO(WordUtils.capitalize(cat.replace("-", " ")), cat)
        ).toList();
    }
}
