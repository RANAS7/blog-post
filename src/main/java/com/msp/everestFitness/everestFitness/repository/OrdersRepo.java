package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.OrderItems;
import com.msp.everestFitness.everestFitness.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrdersRepo extends JpaRepository<Orders, UUID> {
    List<OrderItems> getOrderItemsByOrderId(UUID orderId);

    List<Orders> findAllByShippingInfoIdIn(List<UUID> shippingIds);
}
