package com.example.blogPost.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "created_at", updatable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp createdAt;

    @Column(name = "expiry_date", nullable = false)
    private Timestamp expiryDate;
}