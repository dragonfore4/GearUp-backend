package com.sira.rueng.ecommerce.dao;

import com.sira.rueng.ecommerce.model.Cart;
import com.sira.rueng.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserId(Integer userId);
}
