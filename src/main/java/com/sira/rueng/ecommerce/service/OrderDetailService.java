package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.OrderDetailRepository;
import com.sira.rueng.ecommerce.dao.OrderRepository;
import com.sira.rueng.ecommerce.model.OrderDetail;
import com.sira.rueng.ecommerce.model.OrderDetailId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository ordersRepository;

    @Transactional(readOnly = true)
    public List<OrderDetail> getOrderDetailsByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Transactional
    public void deleteOrderDetail(Integer orderId, Integer sequenceId) {
        OrderDetailId orderDetailId = new OrderDetailId(sequenceId, orderId);
        orderDetailRepository.deleteById(orderDetailId);
    }

    @Transactional
    public OrderDetail updateOrderDetail(Integer orderId, Integer sequenceId, OrderDetail orderDetailUpdate) {
        OrderDetailId orderDetailId = new OrderDetailId(sequenceId, orderId);

        OrderDetail existingOrderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Order Detail not found"));

        // Update specific fields
        if (orderDetailUpdate.getQuantity() != null) {
            existingOrderDetail.setQuantity(orderDetailUpdate.getQuantity());
        }
        if (orderDetailUpdate.getPrice() != null) {
            existingOrderDetail.setPrice(orderDetailUpdate.getPrice());
        }
        if (orderDetailUpdate.getProduct() != null) {
            existingOrderDetail.setProduct(orderDetailUpdate.getProduct());
        }

        return orderDetailRepository.save(existingOrderDetail);
    }
}