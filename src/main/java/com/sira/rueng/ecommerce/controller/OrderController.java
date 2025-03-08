package com.sira.rueng.ecommerce.controller;

import com.sira.rueng.ecommerce.model.Order;
import com.sira.rueng.ecommerce.model.OrderDetail;
import com.sira.rueng.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 📌 1. สร้างคำสั่งซื้อใหม่ (สร้างจาก Cart)
    @PostMapping("/orders/{userId}")
    public ResponseEntity<Order> createOrder(@PathVariable Integer userId) {
        Order newOrder = orderService.createOrder(userId);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    // 📌 2. ดึงคำสั่งซื้อทั้งหมด
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> order = orderService.getAllOrders();
        return ResponseEntity.ok(order);
    }

    // 📌 3. ดึงคำสั่งซื้อโดย ID
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // 📌 4. อัปเดตสถานะคำสั่งซื้อ (เช่น "Shipped", "Completed", "Cancelled")
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Integer orderId, @RequestParam String status) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    // 📌 5. ลบคำสั่งซื้อ (และ OrderDetails ที่เกี่ยวข้อง)
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    // 📌 6. ดึง OrderDetails ตาม OrderId
    @GetMapping("/orders/{orderId}/details")
    public ResponseEntity<List<OrderDetail>> getOrderDetails(@PathVariable Integer orderId) {
        List<OrderDetail> orderDetails = orderService.getOrderDetailsByOrderId(orderId);
        return ResponseEntity.ok(orderDetails);
    }
}
