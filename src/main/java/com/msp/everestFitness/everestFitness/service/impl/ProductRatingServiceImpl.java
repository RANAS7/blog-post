package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.dto.ProductRatingRequestDto;
import com.msp.everestFitness.everestFitness.model.ProductRating;
import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Users;
import com.msp.everestFitness.everestFitness.repository.ProductRatingRepo;
import com.msp.everestFitness.everestFitness.service.ProductRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductRatingServiceImpl implements ProductRatingService {

    @Autowired
    private ProductRatingRepo productRatingRepo;

    // Add a new rating
    @Override
    public void addRating(ProductRatingRequestDto request) {
        ProductRating productRating = new ProductRating();
        productRating.setProduct(request.getProduct());
        productRating.setUser(request.getUser());
        productRating.setRating(request.getRating());
        productRating.setReview(request.getReview());

        productRatingRepo.save(productRating);
    }

    // Get all ratings for a product
    @Override
    public List<ProductRating> getRatingsForProduct(Products product) {
        return productRatingRepo.findByProduct(product);
    }

    // Get a rating by ID
    public ProductRating getRatingById(UUID ratingId) {
        return productRatingRepo.findById(ratingId).orElse(null);
    }

    // Delete a rating
    @Override
    public void deleteRating(UUID ratingId) {
        productRatingRepo.deleteById(ratingId);
    }
}
