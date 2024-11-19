package com.msp.everestFitness.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "wishlist", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID wishlistId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @Column(columnDefinition = "Timestamp default current_timestamp", updatable = false)
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @Column(columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt = Timestamp.from(Instant.now());
}