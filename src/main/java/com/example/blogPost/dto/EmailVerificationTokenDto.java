package com.example.blogPost.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class EmailVerificationTokenDto {
    private UUID id;
    private String token;
    private String email;
    private Timestamp createdAt;
    private Timestamp expiryDate;
}
