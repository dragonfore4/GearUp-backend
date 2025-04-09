package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.ProductTypeRepository;
import com.sira.rueng.ecommerce.model.ProductType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductTypeServiceTest {

    @Mock
    private ProductTypeRepository productTypeRepository;

    @InjectMocks
    private ProductTypeService productTypeService;

    // Test getAllProductTypes()
    @Test
    void testGetAllProductTypes() {
        ProductType type1 = new ProductType();
        type1.setId(1);
        type1.setName("Electronics");

        ProductType type2 = new ProductType();
        type2.setId(2);
        type2.setName("Books");

        when(productTypeRepository.findAll()).thenReturn(List.of(type1, type2));

        List<ProductType> result = productTypeService.getAllProductTypes();

        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
        assertEquals("Books", result.get(1).getName());
    }

    // Test getProductTypeById - Found
    @Test
    void testGetProductTypeById_Found() {
        ProductType type = new ProductType();
        type.setId(1);
        type.setName("Clothing");

        when(productTypeRepository.findById(1)).thenReturn(Optional.of(type));

        Optional<ProductType> result = productTypeService.getProductTypeById(1);

        assertTrue(result.isPresent());
        assertEquals("Clothing", result.get().getName());
    }

    // Test getProductTypeById - Not Found
    @Test
    void testGetProductTypeById_NotFound() {
        when(productTypeRepository.findById(1)).thenReturn(Optional.empty());

        Optional<ProductType> result = productTypeService.getProductTypeById(1);

        assertFalse(result.isPresent());
    }

    // Test createProductType
    @Test
    void testCreateProductType() {
        ProductType type = new ProductType();
        type.setName("New Type");

        when(productTypeRepository.save(type)).thenReturn(type);

        ProductType result = productTypeService.createProductType(type);

        assertEquals("New Type", result.getName());
        verify(productTypeRepository, times(1)).save(type);
    }
}
