package com.msp.everestFitness.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "membership_payments")
public class MembershipPayments {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID membershipPaymentId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Members member;
    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt = Timestamp.from(Instant.now());
}
