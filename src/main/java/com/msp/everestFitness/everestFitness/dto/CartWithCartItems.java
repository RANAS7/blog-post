package com.msp.everestFitness.everestFitness.dto;

import com.msp.everestFitness.everestFitness.model.Carts;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CartWithCartItems {
    private UUID cartId; // Cart details
    private List<Long> quantities; // List of quantities
    private List<BigDecimal> prices; // List of prices
    private List<String> names; // List of product names
    private List<String> imageUrls; // List of product image URLs
}
