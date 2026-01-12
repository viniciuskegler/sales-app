package com.viniciuskegler.salesapp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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

    private String description;

    @Column(length = 25)
    private String category;

    private BigDecimal price;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    private BigDecimal rating;

    private Integer stock;

    @Column(length = 20)
    private String brand;

    @Column(length = 15)
    private String sku;

    private String thumbnail;

    @JsonManagedReference
    @OneToMany(mappedBy= "product", cascade = CascadeType.ALL)
    private Set<Review> reviews;
}
