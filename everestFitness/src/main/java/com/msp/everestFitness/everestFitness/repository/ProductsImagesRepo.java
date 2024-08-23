package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductsImagesRepo extends JpaRepository<ProductImages, UUID> {
}
