package com.viniciuskegler.salesapp.product.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @OrderBy("position ASC")
    private List<ProductImage> images;

    @Valid
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @ManyToMany
    @JoinTable(
            name = "products_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
}
