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
public class OrderDetailId implements Serializable {

    @Column(name = "sequence_id")
    private Integer sequenceId;

    @Column(name = "order_id")
    private Integer orderId;

    public OrderDetailId() {

    }

    public OrderDetailId(Integer sequenceId, Integer orderId) {
        this.sequenceId = sequenceId;
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailId that = (OrderDetailId) o;
        return Objects.equals(sequenceId, that.sequenceId) && Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequenceId, orderId);
    }
}
