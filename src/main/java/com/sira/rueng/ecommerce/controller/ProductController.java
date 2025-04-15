package com.sira.rueng.ecommerce.controller;

import com.sira.rueng.ecommerce.model.Product;
import com.sira.rueng.ecommerce.response.ErrorResponse;
import com.sira.rueng.ecommerce.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProductsByPriceRange(
            @RequestParam(defaultValue = "0") double minPrice,
            @RequestParam(defaultValue = "100000") double maxPrice,
            @RequestParam(required = false, defaultValue = "") Integer productTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false, defaultValue = "") String sortBy) {

        // สร้าง Pageable สำหรับ pagination
        Pageable pageable = PageRequest.of(page, limit, getSortOption(sortBy));

        // ดึงข้อมูลสินค้าตามช่วงราคาและ pagination
        Page<Product> productPage = productService.getProductsByFilters(minPrice, maxPrice, productTypeId, pageable);

        // เตรียมข้อมูลที่จะส่งกลับ
        Map<String, Object> response = new HashMap<>();
        response.put("products", productPage.getContent()); // รายการสินค้าที่กรองแล้ว
        response.put("totalItems", productPage.getTotalElements()); // จำนวนสินค้าทั้งหมด
        response.put("totalPages", productPage.getTotalPages()); // จำนวนหน้าทั้งหมด
        response.put("currentPage", productPage.getNumber()); // หน้าปัจจุบัน

        return ResponseEntity.ok(response);
    }


    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        Optional<Product> optionalProduct = productService.getProductById(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            return ResponseEntity.ok(product);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            ErrorResponse error = new ErrorResponse("Product not found", HttpStatus.NO_CONTENT.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Create Product
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("stock") int stock,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("productTypeId") Integer productTypeId) {
        try {
            System.out.println("in create product" + name + " " + description + " " + price + " " + stock + " " + productTypeId);
            System.out.println(image);
            System.out.println(image.getOriginalFilename());
            Product createdProduct = productService.createProduct(name, description, price, stock, image,productTypeId);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
//            return null;
        } catch (Exception e) {
            return new ResponseEntity<>("Product creation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // update
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            ErrorResponse error = new ErrorResponse("User not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // delete
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        // Check if the product exists
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An Error occured while deleting the product"));
        }
    }

    private Sort getSortOption(String sortBy) {
        return switch (sortBy) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            default -> Sort.unsorted();
        };
    }

}
