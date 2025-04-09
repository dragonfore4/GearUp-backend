package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.ProductRepository;
import com.sira.rueng.ecommerce.dao.ProductTypeRepository;
import com.sira.rueng.ecommerce.model.Product;
import com.sira.rueng.ecommerce.model.ProductType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @Mock
    ProductTypeRepository productTypeRepository;

    @Mock
    CloudinaryService cloudinaryService;

    @InjectMocks
    ProductService productService;

    @Test
    void testGetAllProducts() {
        Product p1 = new Product();
        p1.setName("Mouse");

        Product p2 = new Product();
        p2.setName("Keyboard");

        Mockito.when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = productService.getAllProducts();
        Assertions.assertEquals("Mouse", result.get(0).getName());
        Assertions.assertEquals("Keyboard", result.get(1).getName());
    }

    // Test getProductById - Found
    @Test
    void testGetProductById_Found() {
        Product product = new Product();
        product.setId(1);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    // Test getProductById - Not Found
    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(1);
        assertFalse(result.isPresent());
    }

    // Test updateProduct - Found
    @Test
    void testUpdateProduct_Found() {
        Product existing = new Product();
        existing.setId(1);
        existing.setName("old");

        Product updated = new Product();
        updated.setName("New");
        updated.setDescription("New Description");
        updated.setPrice(100.0);
        updated.setStock(10);

        when(productRepository.findById(1)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct(1, updated);

        assertEquals("New", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(100.0, result.getPrice());
        assertEquals(10, result.getStock());
    }

    // Test updateProduct - Found
    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        Product result = productService.updateProduct(1, new Product());
        assertNull(result);
    }

    // Test deleteProduct - Found
    @Test
    void testDeleteProduct_Found() {
        Product product = new Product();
        product.setId(1);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        productService.deleteProduct(1);
        verify(productRepository, times(1)).deleteById(1);
    }

    // Test deleteProduct - Not found
    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(1);
        });

        assertEquals("Product not found with id - 1", exception.getMessage());
    }

    // Test createProduct - Success
    @Test
    void testCreateProduct_Success() throws IOException {
        ProductType productType = new ProductType();
        productType.setId(1);

        MultipartFile mockFile = mock(MultipartFile.class);
//        when(mockFile.getOriginalFilename()).thenReturn("image.jpg");

        Map<String, String> cloudinaryResponse = Map.of("secure_url", "http://image.url");

        when(productTypeRepository.findById(1)).thenReturn(Optional.of(productType));
        when(cloudinaryService.uploadImage(mockFile)).thenReturn(cloudinaryResponse);

        Product savedProduct = new Product();
        savedProduct.setName("New Product");

        when(productRepository.save(any())).thenReturn(savedProduct);

        Product result = productService.createProduct("New Product", "desc", 99.9, 5, mockFile, 1);
        assertEquals("New Product", result.getName());
    }

    // Test createProduct - ProductType Not Found
    @Test
    void testCreateProduct_ProductTypeNotFound() {
        MultipartFile file = mock(MultipartFile.class);
        when(productTypeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productService.createProduct("X", "Y", 10.0, 5, file, 1);
        });
    }

    // Test getProductsByFilters - With productTypeId
    @Test
    void testGetProductsByFilters_WithType() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> mockPage = new PageImpl<>(List.of(new Product()));

        when(productRepository.findByPriceRangeAndProductType(10.0, 100.0, 1, pageable))
                .thenReturn(mockPage);

        Page<Product> result = productService.getProductsByFilters(10.0, 100.0, 1, pageable);
        assertEquals(1, result.getTotalElements());
    }

    // Test getProductsByFilters - Without productTypeId
    @Test
    void testGetProductsByFilters_NoType() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> mockPage = new PageImpl<>(List.of(new Product(), new Product()));

        when(productRepository.findByPriceRange(10.0, 100.0, pageable))
                .thenReturn(mockPage);

        Page<Product> result = productService.getProductsByFilters(10.0, 100.0, null, pageable);
        assertEquals(2, result.getTotalElements());
    }
}
