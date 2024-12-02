package com.msp.everestFitness.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductWithImagesDto {
    private UUID productId;
    private String name;
    private String description;
    private Double price;
    private Double discountedPrice;
    private List<String> imageUrls;
    private Double rating;

    private UUID wishlistId;
}
