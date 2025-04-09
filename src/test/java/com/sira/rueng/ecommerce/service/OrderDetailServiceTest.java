package com.sira.rueng.ecommerce.service;

import com.sira.rueng.ecommerce.dao.OrderDetailRepository;
import com.sira.rueng.ecommerce.dao.OrderRepository;
import com.sira.rueng.ecommerce.model.OrderDetail;
import com.sira.rueng.ecommerce.model.OrderDetailId;
import com.sira.rueng.ecommerce.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderDetailServiceTest {

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderDetailService orderDetailService;

    // Test getOrderDetailsByOrderId
    @Test
    void testGetOrderDetailsByOrderId() {
        OrderDetail detail1 = new OrderDetail();
        OrderDetail detail2 = new OrderDetail();
        when(orderDetailRepository.findByOrderId(1)).thenReturn(List.of(detail1, detail2));

        List<OrderDetail> result = orderDetailService.getOrderDetailsByOrderId(1);
        assertEquals(2, result.size());
        verify(orderDetailRepository, times(1)).findByOrderId(1);
    }

    // Test deleteOrderDetail
    @Test
    void testDeleteOrderDetail() {
        OrderDetailId id = new OrderDetailId(1, 1);

        orderDetailService.deleteOrderDetail(1, 1);

        verify(orderDetailRepository, times(1)).deleteById(id);
    }

    // Test updateOrderDetail - found
    @Test
    void testUpdateOrderDetail_Found() {
        OrderDetailId id = new OrderDetailId(1, 1);

        OrderDetail existing = new OrderDetail();
        existing.setQuantity(2);
        existing.setPrice(100.0);

        OrderDetail updated = new OrderDetail();
        updated.setQuantity(5);
        updated.setPrice(120.0);
        Product newProduct = new Product();
        newProduct.setId(99);
        updated.setProduct(newProduct);

        when(orderDetailRepository.findById(id)).thenReturn(Optional.of(existing));
        when(orderDetailRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderDetail result = orderDetailService.updateOrderDetail(1, 1, updated);

        assertEquals(5, result.getQuantity());
        assertEquals(120.0, result.getPrice());
        assertEquals(99, result.getProduct().getId());
    }

    // Test updateOrderDetail - not found
    @Test
    void testUpdateOrderDetail_NotFound() {
        OrderDetailId id = new OrderDetailId(1, 1);
        when(orderDetailRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            orderDetailService.updateOrderDetail(1, 1, new OrderDetail());
        });

        assertEquals("Order Detail not found", ex.getMessage());
    }
}
