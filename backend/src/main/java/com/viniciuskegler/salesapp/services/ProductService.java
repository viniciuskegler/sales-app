package com.viniciuskegler.salesapp.services;

import com.viniciuskegler.salesapp.dto.ProductCategoryDTO;
import com.viniciuskegler.salesapp.enums.SortOrder;
import com.viniciuskegler.salesapp.exception.RecordNotFoundException;
import com.viniciuskegler.salesapp.model.Product;
import com.viniciuskegler.salesapp.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Validated
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> getProducts(Integer page, Integer pageSize, String sortBy,
                                     SortOrder sortOrder) {
        Sort sort = Sort.by(sortBy);
        if(sortOrder.equals(SortOrder.ASC)){
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return productRepository.findAll(pageable);
    }

    public Product findById(@NotNull @Positive Long id){
        return productRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
    }

    public ProductCategoryDTO getCategories(){
        return null;
    }
}
