package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.CartDetailRepository;
import com.sira.rueng.ecommerce.dao.CartRepository;
import com.sira.rueng.ecommerce.dao.ProductRepository;
import com.sira.rueng.ecommerce.model.Cart;
import com.sira.rueng.ecommerce.model.CartDetail;
import com.sira.rueng.ecommerce.model.CartDetailId;
import com.sira.rueng.ecommerce.model.Product;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartDetailService {

    private CartDetailRepository cartDetailRepository;
    private CartRepository cartRepository;
    private ProductRepository productRepository;

    @Autowired
    public CartDetailService(CartDetailRepository cartDetailRepository, CartRepository cartRepository, ProductRepository productRepository) {
        this.cartDetailRepository = cartDetailRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public CartDetail addProductToCart(Integer cartId, Integer productId, Integer quantity) {

        // find cart
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        // find product
        Optional<Product> productOptional = productRepository.findById(productId);

        if (cartOptional.isEmpty()) {
            throw new EntityNotFoundException("Cart not found with id: " + cartId);
        }

        if (productOptional.isEmpty()) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }

        Cart cart = cartOptional.get();
        Product product = productOptional.get();

        Optional<CartDetail> existingCartDetailOptional = cartDetailRepository.findByCartAndProduct(cart, product);
        CartDetail cartDetail;

        if (existingCartDetailOptional.isPresent()) {
            // update existing cart detail
            cartDetail = existingCartDetailOptional.get();
            cartDetail.setQuantity(cartDetail.getQuantity() + quantity);
        } else {
            // create a new cart detail
            cartDetail = new CartDetail();
            CartDetailId newId = new CartDetailId(generateSequenceId(cartId), cartId);
            cartDetail.setId(newId);
            cartDetail.setCart(cart);
            cartDetail.setProduct(product);
            cartDetail.setQuantity(quantity);
        }

        return cartDetailRepository.save(cartDetail);

    }

    private Integer generateSequenceId(Integer cartId) {
        Integer maxSequenceId = cartDetailRepository.findMaxSequenceIdByCartId(cartId);
        // If maxSequenceId is null, return 1, indicating the first sequence ID
        return (maxSequenceId != null ? maxSequenceId : 0) + 1;
    }


    public List<CartDetail> getCartDetailsByCart(Integer cartId) {
        return cartDetailRepository.findByCartId(cartId);
    }

    @Transactional
    public CartDetail updateCartItemQuantity(Integer cartId, Integer productId, Integer quantity) {
        // ค้นหา CartDetail โดยใช้ cartId และproductId
        Optional<CartDetail> optionalCartDetail = cartDetailRepository.findByCartIdAndProductId(cartId, productId);


        // ตรวจสอบว่า CartDetail พบหรือไม่
        if (optionalCartDetail.isPresent()) {
            // ถ้าพบ, อัปเดตจำนวนสินค้า
            CartDetail cartDetail = optionalCartDetail.get();
            cartDetail.setQuantity(cartDetail.getQuantity() + quantity);  // เพิ่มจำนวนสินค้าที่จะอัปเดต
            return cartDetailRepository.save(cartDetail);  // บันทึกการอัปเดตลงในฐานข้อมูล
        } else {
            // ถ้าไม่พบ CartDetail ให้โยนข้อผิดพลาด
            throw new RuntimeException("CartDetail not found for the given sequenceId and cartId");
        }
    }


    @Transactional
    public void removeProductFromCart(Integer cartId, Integer productId) {
        // find cart
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        // find product
        Optional<Product> productOptional = productRepository.findById(productId);

        if (cartOptional.isEmpty()) {
            throw new EntityNotFoundException("Cart not found with id: " + cartId);
        }

        if (productOptional.isEmpty()) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }

        Cart cart = cartOptional.get();
        Product product = productOptional.get();

        Optional<CartDetail> existingCartDetailOptional = cartDetailRepository.findByCartAndProduct(cart, product);
        CartDetail cartDetail;
        if (existingCartDetailOptional.isPresent()) {
            cartDetail = existingCartDetailOptional.get();
            cartDetailRepository.delete(cartDetail);
        } else {
            throw new EntityNotFoundException("not found Cart Detail");
        }

    }

}
