package com.msp.everestFitness.dto;

import com.msp.everestFitness.model.Products;
import com.msp.everestFitness.model.Users;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Products products;
    private Users users;
    private Long quantity;
    private BigDecimal price;
}

