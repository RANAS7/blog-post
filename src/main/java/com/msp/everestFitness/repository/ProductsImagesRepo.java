package com.msp.everestFitness.repository;

import com.msp.everestFitness.model.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductsImagesRepo extends JpaRepository<ProductImages, UUID> {
    List<ProductImages> findByProduct_ProductId(UUID productId);
}
