package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.ProductRating;
import com.msp.everestFitness.everestFitness.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRatingRepo extends JpaRepository<ProductRating, UUID> {
    List<ProductRating> findByProduct(Products product);
}
