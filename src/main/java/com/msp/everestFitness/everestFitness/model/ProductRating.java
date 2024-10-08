package com.msp.everestFitness.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "product_ratings")
public class ProductRating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ratingId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products products;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @Column(nullable = false)
    private Integer rating;

    private String review;

    @Column(updatable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @Column(insertable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt = Timestamp.from(Instant.now());
}
