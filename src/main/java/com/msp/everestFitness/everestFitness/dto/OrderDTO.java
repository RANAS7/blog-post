package com.msp.everestFitness.everestFitness.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.util.UUID;

@Data
public class OrderDTO {
    private UUID orderId;
    private Timestamp orderDate;
    private double total;
    private String orderStatus;
    private String paymentMethod;
    private String deliveryOption;  // Derived from DeliveryOpt

}
