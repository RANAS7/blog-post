package com.msp.everestFitness.repository;

import com.msp.everestFitness.model.MembershipPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MembershipPaymentsRepo extends JpaRepository<MembershipPayments, UUID> {
}
