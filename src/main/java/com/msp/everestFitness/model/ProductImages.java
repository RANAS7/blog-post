package com.msp.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table
public class ProductImages {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID imageId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Products product;

    @Column(nullable = false)
    private String imageUrl;

    @Column(updatable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp createdAt;

    @Column(insertable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt;
}
