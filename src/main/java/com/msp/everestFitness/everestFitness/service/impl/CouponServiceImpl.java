package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.Coupons;
import com.msp.everestFitness.everestFitness.model.EmailVerificationToken;
import com.msp.everestFitness.everestFitness.repository.CouponRepo;
import com.msp.everestFitness.everestFitness.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponRepo couponRepo;


    @Override
    public void createCoupon(Coupons coupons) {
        if (coupons.getCouponId() != null) {
            Coupons coupons1 = couponRepo.findById(coupons.getCouponId())
                    .orElseThrow(() -> new ResourceNotFoundException("The coupon is not found with the Id: " + coupons.getCouponId()));
            coupons1.setCode(coupons.getCode());
            coupons1.setDescription(coupons.getDescription());
            coupons1.setDiscountType(coupons.getDiscountType());
            coupons1.setDiscountAmount(coupons.getDiscountAmount());
            coupons1.setMaxDiscountAmount(coupons.getMaxDiscountAmount());
            coupons1.setMinimumOrderAmount(coupons.getMinimumOrderAmount());
            coupons1.setValidUntil(coupons.getValidUntil());

            couponRepo.save(coupons1);
        }

        couponRepo.save(coupons);

    }

    // Scheduled cleanup for expired coupons
    @Scheduled(cron = "0 0 * * * ?") // Runs every hour
    public void deactivateExpiredCoupons() {
        List<Coupons> expiredCoupons = couponRepo.findAll()
                .stream()
                .filter(coupon -> coupon.getValidUntil().toInstant().isBefore(Instant.now()))
                .toList();

        // Update isActive status for expired coupons
        for (Coupons coupon : expiredCoupons) {
            coupon.setIsActive(false);
        }

        // Save all updated coupons in a single batch
        couponRepo.saveAll(expiredCoupons);
    }

    @Override
    public List<Coupons> getCoupons() {
        return couponRepo.findAll();
    }

    @Override
    public Coupons getCouponById(UUID couponId) {
        return couponRepo.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("The coupon not found with the Id: " + couponId));
    }

    @Override
    public void deleteCoupon(UUID couponId) {
        couponRepo.deleteById(couponId);
    }
}
