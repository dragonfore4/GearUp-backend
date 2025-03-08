package com.sira.rueng.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cart_detail")
public class CartDetail {

    @EmbeddedId
    private CartDetailId id;

    @ManyToOne
    @JoinColumn(name = "cart_id", insertable = false, updatable = false, nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

}
