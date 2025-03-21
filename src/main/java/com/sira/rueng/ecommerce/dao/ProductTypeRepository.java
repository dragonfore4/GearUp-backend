package com.sira.rueng.ecommerce.dao;

import com.sira.rueng.ecommerce.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Integer> {
}
