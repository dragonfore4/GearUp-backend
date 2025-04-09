package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.ProductRepository;
import com.sira.rueng.ecommerce.dao.ProductTypeRepository;
import com.sira.rueng.ecommerce.model.Product;
import com.sira.rueng.ecommerce.model.ProductType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private ProductTypeRepository productTypeRepository;
    private CloudinaryService cloudinaryService;

    @Autowired
    public ProductService(ProductRepository productRepository, CloudinaryService cloudinaryService, ProductTypeRepository productTypeRepository) {
        this.productRepository = productRepository;
        this.productTypeRepository = productTypeRepository;
        this.cloudinaryService = cloudinaryService;
    }

    //     Read
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    // Update
    public Product updateProduct(Integer id, Product productDetails) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setStock(productDetails.getStock());
            return productRepository.save(product);
        }
        return null;
    }

    // Delete
    public void deleteProduct(Integer id) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            productRepository.deleteById(id);
        } else {
            throw new RuntimeException("Product not found with id - " + id);
        }
    }

    public Product createProduct(String name, String description, double price, int stock, MultipartFile image, int productTypeId) throws IOException {

        ProductType productType = productTypeRepository.findById(productTypeId).orElseThrow(() -> new EntityNotFoundException("ProductType not found with id - " + productTypeId));
//        System.out.println(productType.getId());
//        System.out.println(image.getOriginalFilename());

        Map respones = cloudinaryService.uploadImage(image);
//        System.out.println("success: " + respones.get("secure_url"));

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl((String) respones.get("secure_url"));
        product.setProductType(productType);
        return productRepository.save(product);

    }

    public Page<Product> getProductsByFilters(double minPrice, double maxPrice, Integer productTypeId, Pageable pageable) {
        if (productTypeId != null) {
            return productRepository.findByPriceRangeAndProductType(minPrice, maxPrice, productTypeId, pageable);

        } else {

            // ใช้ query เพื่อกรองสินค้าตามช่วงราคา
            return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
        }
    }
}
