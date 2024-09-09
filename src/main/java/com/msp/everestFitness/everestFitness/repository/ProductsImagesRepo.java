package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductsImagesRepo extends JpaRepository<ProductImages, UUID> {
    List<ProductImages> findByProduct_ProductId(UUID productId);
}
