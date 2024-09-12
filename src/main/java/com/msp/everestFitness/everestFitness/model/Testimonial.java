package com.msp.everestFitness.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "testimonials")
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID testimonialId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private String name;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @Column(name = "updated_at", nullable = false, insertable = false)
    private Timestamp updatedAt = Timestamp.from(Instant.now());
}
