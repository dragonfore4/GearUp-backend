package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.*;
import com.sira.rueng.ecommerce.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock UserRepository userRepository;
    @Mock OrderRepository orderRepository;
    @Mock OrderDetailRepository orderDetailRepository;
    @Mock CartRepository cartRepository;
    @Mock CartDetailRepository cartDetailRepository;
    @Mock ProductRepository productRepository;

    @InjectMocks OrderService orderService;

    // Test createOrder - Success
    @Test
    void testCreateOrder_Success() {
        int userId = 1;

        User user = new User();
        user.setId(userId);

        Product product = new Product();
        product.setId(100);
        product.setPrice(50.0);
        product.setStock(10);

        Cart cart = new Cart();
        cart.setId(1);
        cart.setUser(user);

        CartDetail cartDetail = new CartDetail();
        cartDetail.setCart(cart);
        cartDetail.setProduct(product);
        cartDetail.setQuantity(2);

        Order order = new Order();
        order.setId(1);
        order.setUser(user);
        order.setStatus("Pending");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartDetailRepository.findByCart(cart)).thenReturn(List.of(cartDetail));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1);
            return o;
        });
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(userId);

        assertNotNull(result);
        assertEquals("Pending", result.getStatus());
        assertEquals(100.0, result.getTotalPrice()); // 50 * 2
        verify(orderDetailRepository).saveAll(anyList());
        verify(cartDetailRepository).deleteAll(anyList());
    }

    // Test createOrder - Empty cart
    @Test
    void testCreateOrder_EmptyCart() {
        User user = new User();
        Cart cart = new Cart();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartDetailRepository.findByCart(cart)).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(1);
        });

        assertEquals("No items in the cart to create an order.", ex.getMessage());
    }

    // Test createOrder - Cart not found
    @Test
    void testCreateOrder_CartNotFound() {
        User user = new User();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(1);
        });

        assertEquals("Cart not found", ex.getMessage());
    }

    // Test getAllOrders
    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order(), new Order()));
        List<Order> result = orderService.getAllOrders();
        assertEquals(2, result.size());
    }

    // Test getOrderById - Found
    @Test
    void testGetOrderById_Found() {
        Order order = new Order();
        order.setId(1);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1);
        assertEquals(1, result.getId());
    }

    // Test getOrderById - Not Found
    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            orderService.getOrderById(1);
        });

        assertEquals("Order not found", ex.getMessage());
    }

    // Test updateOrderStatus
    @Test
    void testUpdateOrderStatus() {
        Order order = new Order();
        order.setId(1);
        order.setStatus("Pending");

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.updateOrderStatus(1, "Completed");
        assertEquals("Completed", result.getStatus());
    }

    // Test deleteOrder
    @Test
    void testDeleteOrder() {
        Order order = new Order();
        order.setId(1);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1);

        verify(orderDetailRepository).deleteByOrder(order);
        verify(orderRepository).delete(order);
    }

    // Test getOrderDetailsByOrderId
    @Test
    void testGetOrderDetailsByOrderId() {
        Order order = new Order();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderDetailRepository.findByOrder(order)).thenReturn(List.of(new OrderDetail(), new OrderDetail()));

        List<OrderDetail> result = orderService.getOrderDetailsByOrderId(1);
        assertEquals(2, result.size());
    }

    // Test getOrderByUserId
    @Test
    void testGetOrderByUserId() {
        Order o1 = new Order();
        Order o2 = new Order();

        when(orderRepository.findByUserId(1)).thenReturn(List.of(o1, o2));

        List<Order> result = orderService.getOrderByUserId(1);
        assertEquals(2, result.size());
    }
}
