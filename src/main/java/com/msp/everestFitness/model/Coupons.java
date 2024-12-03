package com.msp.everestFitness.model;

import com.msp.everestFitness.enumrated.DiscountType;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table
public class Coupons {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID couponId;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, columnDefinition = "Decimal(10,2)")
    private double discountAmount;

    @Column(nullable = false, updatable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp validFrom;

    @Column
    private Timestamp validUntil;

    @Column(columnDefinition = "Decimal(10,2)")
    private double minimumOrderAmount;

    @Column( columnDefinition = "Decimal(10,2)")
    private double maxDiscountAmount;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private DiscountType discountType;

    @Column
    private String description;  // Fixed incorrect Column annotation to String

    @Column(updatable = false)
    private Timestamp createdAt;

    @Column
    private Timestamp updatedAt = Timestamp.from(Instant.now());
}
