package com.msp.everestFitness.repository;

import com.msp.everestFitness.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemsRepo extends JpaRepository<OrderItems, UUID> {
    List<OrderItems> findByOrder_OrderId(UUID orderId);

    @Query("SELECT o.products, SUM(o.quantity) as totalQuantitySold " +
            "FROM OrderItems o " +
            "GROUP BY o.products " +
            "ORDER BY totalQuantitySold DESC " +
            "LIMIT 10")
    List<Object[]> findPopularProducts();


//    @Query("SELECT o.product, SUM(o.quantity) as totalQuantitySold FROM OrderItems o " +
//            "GROUP BY o.product ORDER BY totalQuantitySold DESC")
//    List<Object[]> findPopularProducts(Pageable pageable);


}
