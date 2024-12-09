package com.msp.everestFitness.service.impl;

import com.msp.everestFitness.config.LoginUtil;
import com.msp.everestFitness.dto.ProductRatingRequestDto;
import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.model.ProductRating;
import com.msp.everestFitness.model.Products;
import com.msp.everestFitness.model.Users;
import com.msp.everestFitness.repository.ProductRatingRepo;
import com.msp.everestFitness.repository.UsersRepo;
import com.msp.everestFitness.service.ProductRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductRatingServiceImpl implements ProductRatingService {

    @Autowired
    private ProductRatingRepo productRatingRepo;

    @Autowired
    private LoginUtil loginUtil;

    @Autowired
    private UsersRepo usersRepo;

    // Add a new rating
    @Override
    public void addRating(ProductRatingRequestDto request) {
        Users user = usersRepo.findById(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the Id: " + loginUtil.getCurrentUserId()));

        ProductRating productRating = new ProductRating();
        productRating.setProducts(request.getProducts());
        productRating.setUsers(user);
        productRating.setRating(request.getRating());
        productRating.setReview(request.getReview());

        productRatingRepo.save(productRating);
    }

    // Get all ratings for a product
    @Override
    public List<ProductRating> getRatingsForProduct(Products product) {
        return productRatingRepo.findByProducts(product);
    }

    // Get a rating by ID
    public ProductRating getRatingById(UUID ratingId) {
        return productRatingRepo.findById(ratingId).orElse(null);
    }

    @Override
    public void deleteRating(UUID ratingId, UUID userId) {
        // Fetch the rating by its ID
        ProductRating rating = productRatingRepo.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found"));

        // Check if the rating belongs to the current user
        if (!rating.getUsers().getUserId().equals(userId)) {
            throw new IllegalStateException("You are not allowed to delete this rating.");
        }

        // Proceed with deletion if the user is the creator of the rating
        productRatingRepo.deleteById(ratingId);
    }

    @Override
    public Double getAverageRatingByProductId(UUID productId) {
        return productRatingRepo.getAverageRatingByProductId(productId);
    }
}