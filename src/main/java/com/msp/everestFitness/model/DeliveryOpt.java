package com.msp.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table
public class DeliveryOpt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID optionId;
    @Column(nullable = false)
    private String option;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, columnDefinition = "Decimal(10,2)")
    private double charge;
    @Column(insertable = false, updatable = false)
    private Timestamp createdAt = Timestamp.from(Instant.now());
    @Column(insertable = false)
    private Timestamp updated = Timestamp.from(Instant.now());
}
