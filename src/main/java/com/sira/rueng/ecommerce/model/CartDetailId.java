package com.sira.rueng.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class CartDetailId implements Serializable {
    @Column(name = "sequence_id")
    private Integer sequenceId;

    @Column(name = "cart_id")
    private Integer cartId;

    public CartDetailId() {

    }

    public CartDetailId(Integer sequenceId, Integer cartId) {
        this.sequenceId = sequenceId;
        this.cartId = cartId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartDetailId that = (CartDetailId) o;
        return Objects.equals(sequenceId, that.sequenceId) && Objects.equals(cartId, that.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequenceId, cartId);
    }
}
