package com.msp.everestFitness.everestFitness.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
public class ProductWithImagesDto {
    @Id
    private UUID productId;
    private String name;
    private String description;
    private Double price;
    private Double discountedPrice;
    private List<String> imageUrls;
}
