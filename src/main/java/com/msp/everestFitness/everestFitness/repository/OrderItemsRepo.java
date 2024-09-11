package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemsRepo extends JpaRepository<OrderItems, UUID> {
    List<OrderItems> findByOrder_OrderId(UUID orderId);
}
