package com.msp.everestFitness.everestFitness.dto;

import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Users;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CartItemDto {
    private Products products;
    private Users users;
    private int quantity;
    private BigDecimal price;
}

