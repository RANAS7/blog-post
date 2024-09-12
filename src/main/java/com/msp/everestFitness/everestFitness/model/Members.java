package com.msp.everestFitness.everestFitness.model;

import com.msp.everestFitness.everestFitness.enumrated.MembershipStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.java.Log;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "members")
public class Members {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID memberId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;

    private String address;

    private LocalDate dateOfBirth;

    @Column(name = "membership_start_date", nullable = false, updatable = false)
    private Timestamp membershipStartDate = Timestamp.from(Instant.now());

    private Timestamp membershipEndDate;

    @Enumerated(EnumType.STRING)
    private MembershipStatus membershipStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt = Timestamp.from(Instant.now());
}
