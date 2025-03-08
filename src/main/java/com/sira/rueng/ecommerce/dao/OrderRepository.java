package com.sira.rueng.ecommerce.dao;

import com.sira.rueng.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
