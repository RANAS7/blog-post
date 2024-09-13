package com.msp.everestFitness.everestFitness.service;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailVerificationService {

    void createEmailVerificationToken(String email) throws MessagingException, IOException;

    boolean validateToken(String token);


    boolean verifyEmail(String token);
}
