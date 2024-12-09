package com.example.blogPost.jwt;

import lombok.*;

@Data
@Builder
@ToString
public class JwtRequest {
    private String email;
    private String password;
}
