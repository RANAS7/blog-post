package com.msp.everestFitness.everestFitness.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msp.everestFitness.everestFitness.enumrated.UserType;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isVerified = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType = UserType.USER;

    @Column(columnDefinition = "Timestamp default current_timestamp", updatable = false)
    private Timestamp createdAt;

    @Column(insertable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt;

}
