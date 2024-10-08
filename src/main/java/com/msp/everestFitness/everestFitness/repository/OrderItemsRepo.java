package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemsRepo extends JpaRepository<OrderItems, UUID> {
    List<OrderItems> findByOrder_OrderId(UUID orderId);

    // Find the top 10 popular products based on total quantity sold
    @Query("SELECT o.products, SUM(o.quantity) as totalQuantitySold " +
            "FROM OrderItems o " +
            "GROUP BY o.products " +
            "ORDER BY totalQuantitySold DESC")
    List<Object[]> findPopularProducts(Pageable pageable);

}
