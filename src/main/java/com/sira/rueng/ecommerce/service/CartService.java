package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.CartDetailRepository;
import com.sira.rueng.ecommerce.dao.CartRepository;
import com.sira.rueng.ecommerce.dao.UserRepository;
import com.sira.rueng.ecommerce.model.Cart;
import com.sira.rueng.ecommerce.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private CartRepository cartRepository;
    private CartDetailRepository cartDetailRepository;
    private UserRepository userRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartDetailRepository cartDetailRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userRepository = userRepository;
    }

    // สร้าง Cart ใหม่ให้กับ User
    public Cart createCartForUser(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Cart cart = new Cart();
            cart.setUser(user.get());
            return cartRepository.save(cart);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    // ดึง Cart ตาม UserId
    public Optional<Cart> getCartByUserId(Integer userId) {
        return cartRepository.findByUserId(userId);
    }

    public Optional<Cart> getCartByCartId(Integer cartId) {
        return cartRepository.findById(cartId);
    }

    public List<Cart> findALl() {
        return cartRepository.findAll();
    }
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }
}
