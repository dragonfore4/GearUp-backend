package com.sira.rueng.ecommerce.dao;

import com.sira.rueng.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(double minPrice, double maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.productType.id = :productTypeId")
    Page<Product> findByPriceRangeAndProductType(double minPrice, double maxPrice, int productTypeId, Pageable pageable);
}
