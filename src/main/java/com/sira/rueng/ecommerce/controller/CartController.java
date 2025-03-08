package com.sira.rueng.ecommerce.controller;

import com.sira.rueng.ecommerce.model.Cart;
import com.sira.rueng.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CartController {

    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get cart by user ID
    @GetMapping("/carts/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable Integer userId) {
        Optional<Cart> cart = cartService.getCartByUserId(userId);
        if (cart.isPresent()) {
            return ResponseEntity.ok(cart.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found for user ID: " + userId);
        }
    }
}
