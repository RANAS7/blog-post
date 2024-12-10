package com.example.blogPost.service;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailVerificationService {

    void createEmailVerificationToken(String email) throws MessagingException, IOException;

    boolean validateToken(String token);


    void verifyEmail(String token);
}
