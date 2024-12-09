package com.example.blogPost.jwt;

import lombok.*;

@Data
@Builder
@ToString
public class JwtResponse {
    private String jwtToken;
}
