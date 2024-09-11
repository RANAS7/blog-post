package com.msp.everestFitness.everestFitness.dto;

import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Users;
import lombok.Data;


@Data
public class ProductRatingRequestDto {
    private Products products;
    private Users users;
    private Integer rating;
    private String review;
}
