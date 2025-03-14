package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.ProductRepository;
import com.sira.rueng.ecommerce.model.Product;
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
    private CloudinaryService cloudinaryService;

    @Autowired
    public ProductService(ProductRepository productRepository, CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.cloudinaryService = cloudinaryService;
    }

    //     Read
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
//    public Page<Product> getAllProducts(int page, int limit) {
//        Pageable pageable = PageRequest.of(page, limit);
//        return productRepository.findAll(pageable);
//    }

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

    public Product createProduct(String name, String description, double price, int stock, MultipartFile image) throws IOException {
        Map respones = cloudinaryService.uploadImage(image);
//        System.out.println("success: " + respones.get("secure_url"));
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl((String) respones.get("secure_url"));
        return productRepository.save(product);

    }

    public Page<Product> getProductsByPriceRange(double minPrice, double maxPrice, Pageable pageable) {
        // ใช้ query เพื่อกรองสินค้าตามช่วงราคา
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }
}
