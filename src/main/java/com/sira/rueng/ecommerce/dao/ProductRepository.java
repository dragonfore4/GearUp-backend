package com.sira.rueng.ecommerce.dao;

import com.sira.rueng.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
