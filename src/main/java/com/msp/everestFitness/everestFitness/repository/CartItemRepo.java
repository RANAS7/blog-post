package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.CartItems;
import com.msp.everestFitness.everestFitness.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartItemRepo extends JpaRepository<CartItems, UUID> {

    // Corrected query to fetch CartItems by cartId and productId (product_product_id)
    @Query(value = "SELECT ci.cart_item_id, ci.cart_id, ci.product_product_id, ci.quantity, ci.price, ci.created_at, ci.updated_at " +
            "FROM cart_items ci WHERE ci.cart_id = :cartId AND ci.product_product_id = :productId", nativeQuery = true)
    List<CartItems> findByCartAndProduct(@Param("cartId") UUID cartId, @Param("productId") UUID productId);

    @Modifying
    @Query(value = "DELETE FROM cart_items WHERE cart_id = :cartId", nativeQuery = true)
    Iterable<? extends OrderItems> deleteByCartId(@Param("cartId") UUID cartId);

    List<CartItems> findByCarts_cartId(UUID cartId);
}
