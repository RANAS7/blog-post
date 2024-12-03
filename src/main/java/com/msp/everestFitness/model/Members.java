package com.msp.everestFitness.model;

import com.msp.everestFitness.enumrated.MembershipStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table
public class Members {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID memberId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Users users;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;

    private String address;

    private LocalDate dateOfBirth;

    @Column(updatable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp membershipStartDate;

    private Timestamp membershipEndDate;

    @Enumerated(EnumType.STRING)
    private MembershipStatus membershipStatus;

    @Column(insertable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt;
}
