package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.repository.PasswordResetTokenRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;

public interface PasswordResetService {
    void createPasswordResetToken(String email) throws MessagingException;

    boolean validateToken(String token);

    void resetPassword(String token, String newPassword, String confirmPassword);
}
