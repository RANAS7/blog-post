package com.msp.everestFitness.everestFitness.jwt;

import lombok.*;

@Data
@Builder
@ToString
public class JwtRequest {
    private String email;
    private String password;
}
