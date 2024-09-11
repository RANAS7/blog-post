package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.model.OrderItems;

import java.util.List;
import java.util.UUID;

public interface OrderItemService {
    List<OrderItems> findByOrders_orderId(UUID orderId);
}
