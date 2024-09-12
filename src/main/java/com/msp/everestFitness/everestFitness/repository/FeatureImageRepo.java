package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.FeatureImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeatureImageRepo extends JpaRepository<FeatureImage, UUID> {
}
