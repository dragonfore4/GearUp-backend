package com.sira.rueng.ecommerce.request;

public class ProductIdAndQuantityRequest {
    private Integer productId;
    private Integer quantity;

    // getters and setters

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
