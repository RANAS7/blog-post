package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.model.Coupons;

import java.util.List;
import java.util.UUID;

public interface CouponService {
    void createCoupon(Coupons coupons);
    List<Coupons> getCoupons();
    Coupons getCouponById(UUID couponId);
    void deleteCoupon(UUID couponId);

}
