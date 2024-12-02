package com.msp.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
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

    @Column(updatable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp createdAt = Timestamp.from(Instant.now());
    @Column(insertable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt;
}
