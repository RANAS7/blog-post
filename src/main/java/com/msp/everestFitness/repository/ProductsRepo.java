package com.msp.everestFitness.repository;

import com.msp.everestFitness.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductsRepo extends JpaRepository<Products, UUID> {
    List<Products> findBySubcategory_SubcategoryId(UUID subcategoryId);

    List<Products> findByDiscountedPriceGreaterThan(double price);

    List<Products> findByNameContainingIgnoreCase(String productName);
}
