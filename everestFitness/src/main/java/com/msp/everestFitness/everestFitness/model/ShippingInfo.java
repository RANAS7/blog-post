package com.msp.everestFitness.everestFitness.model;

import com.msp.everestFitness.everestFitness.Enumrated.AddressType;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "shipping_information")
public class ShippingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID shippingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AddressType userType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt = Timestamp.from(Instant.now());
}
