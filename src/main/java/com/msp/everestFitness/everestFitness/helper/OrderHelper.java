package com.msp.everestFitness.everestFitness.helper;

import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.*;
import com.msp.everestFitness.everestFitness.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Component
public class OrderHelper {

    @Autowired
    private ShippingInfoRepo shippingInfoRepo;
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private DeliveryOptRepo deliveryOptRepo;
    @Autowired
    private CouponRepo couponRepo;

    public ShippingInfo fetchShippingInfo(UUID shippingId) throws ResourceNotFoundException {
        return shippingInfoRepo.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping info not found with ID: " + shippingId));
    }

    public Users fetchUser(UUID userId) throws ResourceNotFoundException {
        return usersRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    public double calculateOrderTotal(Orders order) {
        return order.getOrderItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
    }

    public void validateMinimumOrderAmount(double total) {
        if (total < 50) {
            throw new IllegalArgumentException("Minimum order amount is $50");
        }
    }

    public double applyCoupon(String couponCode, double total) {
        if (couponCode == null || couponCode.isEmpty()) {
            return 0.0;  // No coupon provided, no discount applied
        }

        // Fetch the coupon from the repository
        Coupons coupon = couponRepo.findByCode(couponCode);
        if (coupon == null || !coupon.getIsActive()) {
            throw new IllegalArgumentException("Invalid or inactive coupon code");
        }

        // Validate coupon's validity period
        Timestamp currentTime = Timestamp.from(Instant.now());
        if (coupon.getValidFrom().after(currentTime) || (coupon.getValidUntil() != null && coupon.getValidUntil().before(currentTime))) {
            throw new IllegalArgumentException("The coupon is not valid for this period");
        }

        // Check if the order meets the coupon's minimum order amount
        if (total < coupon.getMinimumOrderAmount()) {
            throw new IllegalArgumentException("The order does not meet the minimum amount required for the coupon");
        }

        // Apply discount based on discount type (FIXED or PERCENTAGE)
        double discountAmount = switch (coupon.getDiscountType()) {
            case FIXED -> coupon.getDiscountAmount();
            case PERCENTAGE -> total * (coupon.getDiscountAmount() / 100);
            default -> throw new IllegalArgumentException("Invalid discount type");
        };

        // Ensure discount does not exceed the maximum allowed amount
        if (discountAmount > coupon.getMaxDiscountAmount()) {
            discountAmount = coupon.getMaxDiscountAmount();
        }

        // Ensure the discount does not exceed the total order amount
        discountAmount = Math.min(discountAmount, total);

        return discountAmount;  // Return the final discount amount
    }

    public DeliveryOpt fetchDeliveryOption(String deliveryOption) throws IllegalArgumentException {
        return deliveryOptRepo.findByOption(deliveryOption);
    }
}
