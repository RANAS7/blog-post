package com.example.blogPost.ServiceImpl;

import com.example.blogPost.model.PasswordResetToken;
import com.example.blogPost.model.Users;
import com.example.blogPost.repository.PasswordResetTokenRepo;
import com.example.blogPost.repository.UsersRepo;
import com.example.blogPost.service.PasswordResetService;
import com.example.blogPost.utils.MailUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepository;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createPasswordResetToken(String email) throws MessagingException {
        Users users =usersRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Email: " + email));

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setEmail(email);
        passwordResetToken.setExpiryDate(Timestamp.from(Instant.now().plusSeconds(15 * 60)));

        passwordResetTokenRepository.save(passwordResetToken);


        mailUtils.sendPasswordResetEmail(email, passwordResetToken.getToken(), users.getFirstName()+" "+users.getLastName());
    }

    @Override
    public boolean validateToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .filter(t -> t.getExpiryDate().toInstant().isAfter(Instant.now()))
                .isPresent();
    }

    @Override
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (passwordResetToken.getExpiryDate().toInstant().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New Passwords do not equal with Confirm Password");
        }

        Users users = (Users) usersRepo.findByEmail(passwordResetToken.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with the email: " + passwordResetToken.getEmail()));

        // Update user password
        users.setPassword(passwordEncoder.encode(newPassword));
        usersRepo.save(users);

//        Delete token after reset password
        passwordResetTokenRepository.delete(passwordResetToken);
    }


    // Optional: Scheduled cleanup for expired tokens
    @Scheduled(fixedRate = 15 * 60 * 1000) // Every 15 minutes) // Every 24 hours
    public void removeExpiredTokens() {
        List<PasswordResetToken> expiredTokens = passwordResetTokenRepository.findAll()
                .stream()
                .filter(t -> t.getExpiryDate().toInstant().isBefore(Instant.now()))
                .collect(Collectors.toList());

        passwordResetTokenRepository.deleteAll(expiredTokens);
    }
}
