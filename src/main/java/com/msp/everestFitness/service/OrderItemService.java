package com.msp.everestFitness.service;

import com.msp.everestFitness.dto.OrderItemWithImageDto;

import java.util.List;
import java.util.UUID;

public interface OrderItemService {
    List<OrderItemWithImageDto> getOrderItemsOfOrder(UUID orderId);
}
