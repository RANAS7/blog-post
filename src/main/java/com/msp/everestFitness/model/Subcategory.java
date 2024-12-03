package com.msp.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID subcategoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "Timestamp default current_timestamp", updatable = false)
    private Timestamp createdAt;

    @Column(insertable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt;
}
