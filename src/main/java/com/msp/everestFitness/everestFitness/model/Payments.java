package com.msp.everestFitness.everestFitness.model;

import com.msp.everestFitness.everestFitness.enumrated.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "payments")
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @Column(nullable = false)
    private Double amount;

    private String cardType;

    @Column(columnDefinition = "Timestamp default current_timestamp",updatable = false)
    private Timestamp createdAt;

    @Column(insertable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt;
}
