package com.msp.everestFitness.everestFitness.jwt;

import lombok.*;

@Data
@Builder
@ToString
public class JwtResponse {
    private String jwtToken;
    private String username;
}
