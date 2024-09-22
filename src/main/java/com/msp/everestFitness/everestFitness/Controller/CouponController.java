package com.msp.everestFitness.everestFitness.Controller;

import com.msp.everestFitness.everestFitness.model.Coupons;
import com.msp.everestFitness.everestFitness.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/")
    public ResponseEntity<?> createCoupon(@RequestBody Coupons coupons) {
        couponService.createCoupon(coupons);
        return new ResponseEntity<>("Coupon create successfully", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getCoupons(@RequestParam(name = "couponId", required = false) UUID couponId) {
        if (couponId != null) {
            return new ResponseEntity<>(couponService.getCouponById(couponId), HttpStatus.OK);
        }
        return new ResponseEntity<>(couponService.getCoupons(), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteCoupon(@RequestParam UUID couponId) {
        couponService.deleteCoupon(couponId);
        return new ResponseEntity<>("Coupon deleted successfully", HttpStatus.OK);
    }
}
