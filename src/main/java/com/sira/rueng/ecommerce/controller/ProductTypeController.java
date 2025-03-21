package com.sira.rueng.ecommerce.controller;

// ✅ ProductType Controller

import com.sira.rueng.ecommerce.model.ProductType;
import com.sira.rueng.ecommerce.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product-types")
public class ProductTypeController {

    private ProductTypeService productTypeService;

    @Autowired
    public ProductTypeController(ProductTypeService productTypeService) {
        this.productTypeService = productTypeService;
    }

    @GetMapping
    public ResponseEntity<List<ProductType>> getAllProductTypes() {
        return ResponseEntity.ok(productTypeService.getAllProductTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductTypeById(@PathVariable Integer id) {
        Optional<ProductType> productType = productTypeService.getProductTypeById(id);
        return productType.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductType> createProductType(@RequestBody ProductType productType) {
        return ResponseEntity.ok(productTypeService.createProductType(productType));
    }
}