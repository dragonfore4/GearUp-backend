package com.sira.rueng.ecommerce.dao;

import com.sira.rueng.ecommerce.model.Order;
import com.sira.rueng.ecommerce.model.OrderDetail;
import com.sira.rueng.ecommerce.model.OrderDetailId;
import com.sira.rueng.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    List<OrderDetail> findByOrder(Order order);
    void deleteByOrder(Order order);
    List<OrderDetail> findByOrderId(Integer orderId);

}
