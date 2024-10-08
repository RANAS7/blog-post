package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentsRepo extends JpaRepository<Payments, UUID> {
    Payments findByOrders_orderId(UUID orderId);}
