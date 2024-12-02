package com.msp.everestFitness.jwt;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
public class JwtResponse {
//    private UUID userID;
    private String jwtToken;
//    private String username;
//    private String userType;
}
