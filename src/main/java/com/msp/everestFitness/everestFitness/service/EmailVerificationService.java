package com.msp.everestFitness.everestFitness.service;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailVerificationService {

    void createEmailVerificaionToken(String email) throws MessagingException, IOException;

    boolean validateToken(String token);


    void verifyEmail(String token);
}
