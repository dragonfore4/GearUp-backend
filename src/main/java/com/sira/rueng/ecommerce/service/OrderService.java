package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.*;
import com.sira.rueng.ecommerce.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartDetailRepository cartDetailRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrder(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartDetail> cartDetails = cartDetailRepository.findByCart(cart);
        if (cartDetails.isEmpty()) {
            throw new RuntimeException("No items in the cart to create an order.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("Pending");
        order.setTotalPrice(0.0);

        // Save the order first
        Order savedOrder = orderRepository.save(order);

        double totalOrderPrice = 0.0;

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (int i = 0; i < cartDetails.size(); i++) {
            CartDetail cartDetail = cartDetails.get(i);

            // Create composite key
            OrderDetailId orderDetailId = new OrderDetailId(i + 1, savedOrder.getId());

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setId(orderDetailId);
            orderDetail.setOrder(savedOrder);
            orderDetail.setProduct(cartDetail.getProduct());
            orderDetail.setQuantity(cartDetail.getQuantity());
            orderDetail.setPrice(cartDetail.getProduct().getPrice() * cartDetail.getQuantity());

            totalOrderPrice += orderDetail.getPrice();
            orderDetails.add(orderDetail);

            // Update product stock
            Product product = cartDetail.getProduct();
            product.setStock(product.getStock() - cartDetail.getQuantity());
            productRepository.save(product);
        }

        // Save all order details at once
        orderDetailRepository.saveAll(orderDetails);

        savedOrder.setTotalPrice(totalOrderPrice);
        orderRepository.save(savedOrder);

        // Clear the cart
        cartDetailRepository.deleteAll(cartDetails);

        return savedOrder;
    }

    // 📌 2. ดึงคำสั่งซื้อทั้งหมด
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 📌 3. ดึงคำสั่งซื้อโดย ID
    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    // 📌 4. อัปเดตสถานะคำสั่งซื้อ
    public Order updateOrderStatus(Integer orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // 📌 5. ลบคำสั่งซื้อ (และ OrderDetails ที่เกี่ยวข้อง)
    public void deleteOrder(Integer orderId) {
        Order order = getOrderById(orderId);
        orderDetailRepository.deleteByOrder(order);
        orderRepository.delete(order);
    }

    // 📌 6. ดึง OrderDetails ตาม OrderId
    public List<OrderDetail> getOrderDetailsByOrderId(Integer orderId) {
        Order order = getOrderById(orderId);
        return orderDetailRepository.findByOrder(order);
    }
}
