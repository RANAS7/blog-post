package com.msp.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID categoryId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(updatable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp createdAt;

    @Column(insertable = false)
    private Timestamp updatedAt;
}