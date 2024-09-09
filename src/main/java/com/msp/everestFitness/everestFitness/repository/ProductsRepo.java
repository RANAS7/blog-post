package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.Products;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductsRepo extends JpaRepository<Products, UUID> {
}
