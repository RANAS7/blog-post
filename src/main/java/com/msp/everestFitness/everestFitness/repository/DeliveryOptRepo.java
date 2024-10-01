package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.DeliveryOpt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryOptRepo extends JpaRepository<DeliveryOpt, UUID> {
    DeliveryOpt findByOption(String deliveryOpt);
}
