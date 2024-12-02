package com.msp.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "carts")
public class Carts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID cartId;

    @OneToOne(fetch = FetchType.LAZY)
    private Users users;

    @Column(columnDefinition = "Timestamp default current_timestamp")
    private Timestamp createdAt = Timestamp.from(Instant.now());
}
