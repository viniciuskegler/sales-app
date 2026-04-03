package com.viniciuskegler.salesapp.product;

import com.viniciuskegler.salesapp.product.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> getAllProductsCategories();

    @Query("""
            SELECT p FROM Product p
            JOIN p.tags t
            WHERE t IN (SELECT t2 FROM Product p2 JOIN p2.tags t2 WHERE p2.id = :productId)
            AND p.id <> :productId
            GROUP BY p
            ORDER BY COUNT(t) DESC
            """)
    List<Product> findRelatedProducts(@Param("productId") Long productId, Pageable pageable);
}
