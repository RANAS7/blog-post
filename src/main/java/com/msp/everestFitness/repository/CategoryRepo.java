package com.msp.everestFitness.repository;

import com.msp.everestFitness.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepo extends JpaRepository<Category, UUID> {
//    void delete(UUID id);
}
