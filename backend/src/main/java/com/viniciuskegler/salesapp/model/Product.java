package com.viniciuskegler.salesapp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(length = 25, nullable = false)
    private String category;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    private BigDecimal rating;

    private Integer stock;

    @Column(length = 20, nullable = false)
    private String brand;

    @Column(length = 15, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String thumbnail;

    @Valid
    @JsonManagedReference
    @OneToMany(mappedBy= "product", cascade = CascadeType.ALL)
    private Set<Review> reviews;
}
