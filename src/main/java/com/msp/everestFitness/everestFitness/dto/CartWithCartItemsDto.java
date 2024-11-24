package com.msp.everestFitness.everestFitness.dto;

import com.msp.everestFitness.everestFitness.model.Carts;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CartWithCartItemsDto {
    private UUID cartId;
    private UUID itemId;
    private Long quantities; // List of quantities
    private BigDecimal prices; // List of prices
    private String names; // List of product names
    private String imageUrls; // List of product image URLs
}
