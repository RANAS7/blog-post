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
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID testimonialId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "Timestamp default current_timestamp", updatable = false, insertable = false)
    private Timestamp createdAt;

    @Column(columnDefinition = "Timestamp default current_timestamp", insertable = false)
    private Timestamp updatedAt;
}