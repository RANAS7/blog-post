package com.msp.everestFitness.repository;

import com.msp.everestFitness.model.ProductRating;
import com.msp.everestFitness.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRatingRepo extends JpaRepository<ProductRating, UUID> {
    List<ProductRating> findByProducts(Products product);

    @Query("SELECT AVG(pr.rating) FROM ProductRating pr WHERE pr.products.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") UUID productId);
}
