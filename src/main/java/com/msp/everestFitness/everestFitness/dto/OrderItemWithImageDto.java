package com.msp.everestFitness.everestFitness.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemWithImageDto {
    private UUID orderItemId;
    private UUID productId;
    private String productName;
    private Long quantity;
    private double price;
    private double totalAmt;
    private String imageUrl;

}
