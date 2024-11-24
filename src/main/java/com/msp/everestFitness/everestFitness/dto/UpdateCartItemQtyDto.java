package com.msp.everestFitness.everestFitness.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCartItemQtyDto {
    private UUID itemId;
    private Long quantity;
}
