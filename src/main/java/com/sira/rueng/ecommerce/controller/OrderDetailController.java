package com.sira.rueng.ecommerce.controller;

import com.sira.rueng.ecommerce.model.OrderDetail;
import com.sira.rueng.ecommerce.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @GetMapping("/{orderId}")
    public ResponseEntity<List<OrderDetail>> getOrderDetailsByOrderId(@PathVariable Integer orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        return ResponseEntity.ok(orderDetails);
    }

    @DeleteMapping("/{orderId}/{sequenceId}")
    public ResponseEntity<Void> deleteOrderDetail(
            @PathVariable Integer orderId,
            @PathVariable Integer sequenceId) {
        orderDetailService.deleteOrderDetail(orderId, sequenceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderId}/{sequenceId}")
    public ResponseEntity<OrderDetail> updateOrderDetail(
            @PathVariable Integer orderId,
            @PathVariable Integer sequenceId,
            @RequestBody OrderDetail orderDetailUpdate) {
        OrderDetail updatedOrderDetail = orderDetailService.updateOrderDetail(orderId, sequenceId, orderDetailUpdate);
        return ResponseEntity.ok(updatedOrderDetail);
    }
}