package com.sira.rueng.ecommerce.dao;

import com.sira.rueng.ecommerce.model.Cart;
import com.sira.rueng.ecommerce.model.CartDetail;
import com.sira.rueng.ecommerce.model.CartDetailId;
import com.sira.rueng.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, CartDetailId> {
    List<CartDetail> findByCart(Cart cart);

    // Find specific cart detail by cart and product
    Optional<CartDetail> findByCartAndProduct(Cart cart, Product product);

    // Find cart details by a specific cart ID
    List<CartDetail> findByCartId(Integer cartId);

    Optional<CartDetail> findByCartIdAndProductId(Integer cartId, Integer productId);

    // Find a specific cart detail by sequence ID and cart ID
    Optional<CartDetail> findByIdSequenceIdAndCartId(Integer sequenceId, Integer cartId);

    @Query("SELECT MAX(cd.id.sequenceId) FROM CartDetail cd WHERE cd.id.cartId = :cartId")
    Integer findMaxSequenceIdByCartId(@Param("cartId") Integer cartId);
}
