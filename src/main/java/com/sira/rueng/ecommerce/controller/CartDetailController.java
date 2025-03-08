package com.sira.rueng.ecommerce.controller;

import com.sira.rueng.ecommerce.model.CartDetail;
import com.sira.rueng.ecommerce.request.ProductIdAndQuantityRequest;
import com.sira.rueng.ecommerce.service.CartDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartDetail")
public class CartDetailController {

    private CartDetailService cartDetailService;

    @Autowired
    public CartDetailController(CartDetailService cartDetailService) {
        this.cartDetailService = cartDetailService;
    }

    // เพิ่มสินค้าไปยัง Cart
    @PostMapping("/{cartId}/addProduct")
    public ResponseEntity<?> addProductToCart(@PathVariable Integer cartId,
                                              @RequestBody ProductIdAndQuantityRequest  request
                                              ) {
        try {
            CartDetail cartDetail = cartDetailService.addProductToCart(cartId, request.getProductId(), request.getQuantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(cartDetail);  // ส่งกลับ CartDetail ที่เพิ่มสินค้าไป
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());  // กรณี Cart หรือ Product ไม่พบ
        }
    }

    // อัปเดตจำนวนสินค้าใน Cart Detail ตาม cart กับ product
    @PutMapping("/{cartId}/updateProduct")
    public ResponseEntity<?> updateProductQuantity(@PathVariable Integer cartId,
                                                   @RequestBody ProductIdAndQuantityRequest cartItemRequest) {
        try {
            CartDetail updatedCartDetail = cartDetailService.updateCartItemQuantity(cartId, cartItemRequest.getProductId(), cartItemRequest.getQuantity());
            return ResponseEntity.ok(updatedCartDetail);  // ส่งกลับ CartDetail ที่อัปเดตจำนวนสินค้า
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());  // กรณีสินค้าไม่พบ
        }
    }

    // ลบสินค้าออกจาก Cart
    @DeleteMapping("/{cartId}/removeProduct")
    public ResponseEntity<?> removeProductFromCart(@PathVariable Integer cartId,
                                                   @RequestParam Integer productId) {
        try {
            cartDetailService.removeProductFromCart(cartId, productId);
            return ResponseEntity.noContent().build();  // ส่งกลับ HTTP 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());  // กรณีสินค้าไม่พบ
        }
    }

    // ดึงรายการสินค้าทั้งหมดใน Cart
    @GetMapping("/{cartId}/details")
    public ResponseEntity<List<CartDetail>> getCartDetails(@PathVariable Integer cartId) {
        List<CartDetail> cartDetails = cartDetailService.getCartDetailsByCart(cartId);
        return ResponseEntity.ok(cartDetails);  // ส่งกลับรายการสินค้าทั้งหมดใน Cart
    }
}

