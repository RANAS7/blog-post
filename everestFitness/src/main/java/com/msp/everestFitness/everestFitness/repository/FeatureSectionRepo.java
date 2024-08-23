package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.FeatureSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeatureSectionRepo extends JpaRepository<FeatureSection, UUID> {
}
