package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.CartDetailRepository;
import com.sira.rueng.ecommerce.dao.CartRepository;
import com.sira.rueng.ecommerce.dao.ProductRepository;
import com.sira.rueng.ecommerce.model.Cart;
import com.sira.rueng.ecommerce.model.CartDetail;
import com.sira.rueng.ecommerce.model.CartDetailId;
import com.sira.rueng.ecommerce.model.Product;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartDetailServiceTest {

    @Mock
    private CartDetailRepository cartDetailRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartDetailService cartDetailService;

    // Test addProductToCart - new product
    @Test
    void testAddProductToCart_NewItem() {
        int cartId = 1;
        int productId = 100;
        int quantity = 2;

        Cart cart = new Cart();
        cart.setId(cartId);

        Product product = new Product();
        product.setId(productId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartDetailRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());
        when(cartDetailRepository.findMaxSequenceIdByCartId(cartId)).thenReturn(null);

        CartDetail savedCartDetail = new CartDetail();
        savedCartDetail.setQuantity(quantity);
        when(cartDetailRepository.save(any())).thenReturn(savedCartDetail);

        CartDetail result = cartDetailService.addProductToCart(cartId, productId, quantity);

        assertEquals(quantity, result.getQuantity());
    }

    // Test addProductToCart - existing product
    @Test
    void testAddProductToCart_ExistingItem() {
        int cartId = 1;
        int productId = 100;
        int quantity = 3;

        Cart cart = new Cart();
        cart.setId(cartId);

        Product product = new Product();
        product.setId(productId);

        CartDetail existingDetail = new CartDetail();
        existingDetail.setQuantity(2);
        existingDetail.setCart(cart);
        existingDetail.setProduct(product);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartDetailRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingDetail));
        when(cartDetailRepository.save(existingDetail)).thenReturn(existingDetail);

        CartDetail result = cartDetailService.addProductToCart(cartId, productId, quantity);

        assertEquals(5, result.getQuantity()); // 2 + 3
    }

    // Test addProductToCart - cart not found
    @Test
    void testAddProductToCart_CartNotFound() {
        when(cartRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            cartDetailService.addProductToCart(1, 100, 1);
        });

        assertEquals("Cart not found with id: 1", ex.getMessage());
    }

    // Test addProductToCart - product not found
    @Test
    void testAddProductToCart_ProductNotFound() {
        Cart cart = new Cart();
        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        when(productRepository.findById(100)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            cartDetailService.addProductToCart(1, 100, 1);
        });

        assertEquals("Product not found with id: 100", ex.getMessage());
    }

    // Test getCartDetailsByCart
    @Test
    void testGetCartDetailsByCart() {
        List<CartDetail> mockList = List.of(new CartDetail(), new CartDetail());
        when(cartDetailRepository.findByCartId(1)).thenReturn(mockList);

        List<CartDetail> result = cartDetailService.getCartDetailsByCart(1);
        assertEquals(2, result.size());
    }

    // Test updateCartItemQuantity - found
    @Test
    void testUpdateCartItemQuantity_Found() {
        CartDetail cartDetail = new CartDetail();
        cartDetail.setQuantity(1);

        when(cartDetailRepository.findByCartIdAndProductId(1, 100)).thenReturn(Optional.of(cartDetail));
        when(cartDetailRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CartDetail result = cartDetailService.updateCartItemQuantity(1, 100, 2);
        assertEquals(3, result.getQuantity());
    }

    // Test updateCartItemQuantity - not found
    @Test
    void testUpdateCartItemQuantity_NotFound() {
        when(cartDetailRepository.findByCartIdAndProductId(1, 100)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            cartDetailService.updateCartItemQuantity(1, 100, 2);
        });

        assertEquals("CartDetail not found for the given sequenceId and cartId", ex.getMessage());
    }

    // Test removeProductFromCart - found
    @Test
    void testRemoveProductFromCart_Found() {
        Cart cart = new Cart();
        cart.setId(1);

        Product product = new Product();
        product.setId(100);

        CartDetail cartDetail = new CartDetail();
        cartDetail.setCart(cart);
        cartDetail.setProduct(product);

        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        when(productRepository.findById(100)).thenReturn(Optional.of(product));
        when(cartDetailRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartDetail));

        cartDetailService.removeProductFromCart(1, 100);
        verify(cartDetailRepository, times(1)).delete(cartDetail);
    }

    // Test removeProductFromCart - not found
    @Test
    void testRemoveProductFromCart_NotFound() {
        Cart cart = new Cart();
        cart.setId(1);

        Product product = new Product();
        product.setId(100);

        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        when(productRepository.findById(100)).thenReturn(Optional.of(product));
        when(cartDetailRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            cartDetailService.removeProductFromCart(1, 100);
        });

        assertEquals("not found Cart Detail", ex.getMessage());
    }

    // Test removeProductFromCart - cart or product not found
    @Test
    void testRemoveProductFromCart_CartOrProductNotFound() {
        when(cartRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex1 = assertThrows(EntityNotFoundException.class, () -> {
            cartDetailService.removeProductFromCart(1, 100);
        });
        assertEquals("Cart not found with id: 1", ex1.getMessage());

        // case product not found
        when(cartRepository.findById(1)).thenReturn(Optional.of(new Cart()));
        when(productRepository.findById(100)).thenReturn(Optional.empty());

        EntityNotFoundException ex2 = assertThrows(EntityNotFoundException.class, () -> {
            cartDetailService.removeProductFromCart(1, 100);
        });
        assertEquals("Product not found with id: 100", ex2.getMessage());
    }
}
