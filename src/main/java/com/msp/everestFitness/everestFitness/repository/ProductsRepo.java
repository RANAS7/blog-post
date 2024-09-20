package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductsRepo extends JpaRepository<Products, UUID> {
    List<Products> findBySubcategory_SubcategoryId(UUID subcategoryId);

    List<Products> findByDiscountedPriceGreaterThan(double price);
}
