package com.msp.everestFitness.service;

import jakarta.mail.MessagingException;

public interface PasswordResetService {
    void createPasswordResetToken(String email) throws MessagingException;

    boolean validateToken(String token);

    void resetPassword(String token, String newPassword, String confirmPassword);
}
