package com.msp.everestFitness.everestFitness.model;

import com.msp.everestFitness.everestFitness.enumrated.DiscountType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "coupons")
public class Coupons {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID couponId;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "discount_amount", nullable = false, columnDefinition = "Decimal(10,2)" )
    private double discountAmount;

    @Column(name = "valid_from", nullable = false, updatable = false)
    private Timestamp validFrom = Timestamp.from(Instant.now());

    @Column(name = "valid_until", columnDefinition = "Decimal(10,2)")
    private Timestamp validUntil;

    @Column(name = "minimum_order_amount", columnDefinition = "Decimal(10,2)")
    private double minimumOrderAmount;

    @Column(name = "max_discount_amount")
    private double maxDiscountAmount;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "description")
    private String description;  // Fixed incorrect Column annotation to String

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
