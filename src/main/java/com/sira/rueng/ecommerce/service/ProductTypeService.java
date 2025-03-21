package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.ProductTypeRepository;
import com.sira.rueng.ecommerce.model.ProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductTypeService {

    private ProductTypeRepository productTypeRepository;

    @Autowired
    public ProductTypeService(ProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    // READ
    public List<ProductType> getAllProductTypes() {
        return productTypeRepository.findAll();
    }

    // READ BY ID
    public Optional<ProductType> getProductTypeById(Integer id) {
        return productTypeRepository.findById(id);
    }

    // CREATE
    public ProductType createProductType(ProductType productType) {
        return productTypeRepository.save(productType);
    }
}
