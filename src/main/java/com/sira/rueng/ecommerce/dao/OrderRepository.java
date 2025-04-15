package com.sira.rueng.ecommerce.dao;

import com.sira.rueng.ecommerce.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);
    Page<Order> findByStatus(String status, Pageable pageable);

}
