package com.msp.everestFitness.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "order_items")
public class OrderItems {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt = Timestamp.from(Instant.now());
}