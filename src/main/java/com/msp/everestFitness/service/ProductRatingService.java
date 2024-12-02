package com.msp.everestFitness.service;

import com.msp.everestFitness.dto.ProductRatingRequestDto;
import com.msp.everestFitness.model.ProductRating;
import com.msp.everestFitness.model.Products;

import java.util.List;
import java.util.UUID;

public interface ProductRatingService {

    // Add a new rating
    void addRating(ProductRatingRequestDto request);

    // Get all ratings for a product
    List<ProductRating> getRatingsForProduct(Products product);

    // Delete a rating
    void deleteRating(UUID ratingId, UUID userId);

    Double getAverageRatingByProductId(UUID productId);
}
