package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.Coupons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CouponRepo extends JpaRepository<Coupons, UUID> {
    Coupons findByCode(String couponCode);
}
