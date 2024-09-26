package com.msp.everestFitness.everestFitness.dto;

import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Users;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Products products;
    private Users users;
    private int quantity;
    private BigDecimal price;
}

