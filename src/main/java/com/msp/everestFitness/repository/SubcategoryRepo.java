package com.msp.everestFitness.repository;

import com.msp.everestFitness.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubcategoryRepo extends JpaRepository<Subcategory, UUID> {
    List<Subcategory> findByCategory_CategoryId(UUID category);
}