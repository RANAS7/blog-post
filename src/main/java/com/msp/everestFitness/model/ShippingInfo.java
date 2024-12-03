package com.msp.everestFitness.model;

import com.msp.everestFitness.enumrated.AddressType;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table
public class ShippingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID shippingId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Users users;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @Column(columnDefinition = "Timestamp default current_timestamp", updatable = false)
    private Timestamp createdAt;

    @Column(insertable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt;



    @Transient
    private String email;

    @Transient
    private String firstName;

    @Transient
    private String lastName;
}