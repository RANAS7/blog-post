package com.msp.everestFitness.everestFitness.model;

import com.msp.everestFitness.everestFitness.enumrated.MembershipStatus;
import jakarta.persistence.*;
import lombok.Data;

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

    @Column(updatable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp membershipStartDate ;

    private Timestamp membershipEndDate;

    @Enumerated(EnumType.STRING)
    private MembershipStatus membershipStatus;

    @Column(insertable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt;
}
