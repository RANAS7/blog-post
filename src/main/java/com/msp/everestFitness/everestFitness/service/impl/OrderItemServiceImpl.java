package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.model.OrderItems;
import com.msp.everestFitness.everestFitness.repository.OrderItemsRepo;
import com.msp.everestFitness.everestFitness.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    private OrderItemsRepo orderItemsRepo;

    @Override
    public List<OrderItems> findByOrders_orderId(UUID orderId) {
        return orderItemsRepo.findByOrder_OrderId(orderId);
    }
}
