package com.msp.everestFitness.repository;

import com.msp.everestFitness.model.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentsRepo extends JpaRepository<Payments, UUID> {
    Payments findByOrders_orderId(UUID orderId);}
