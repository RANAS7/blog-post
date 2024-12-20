package com.example.blogPost.ServiceImpl;

import com.example.blogPost.model.EmailVerificationToken;
import com.example.blogPost.model.Users;
import com.example.blogPost.repository.EmailVerificationRepo;
import com.example.blogPost.repository.UsersRepo;
import com.example.blogPost.service.EmailVerificationService;
import com.example.blogPost.utils.MailUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private EmailVerificationRepo emailVerificationRepogitory;

    @Autowired
    private MailUtils mailUtils;

    @Override
    public void createEmailVerificationToken(String email) throws MessagingException, IOException {
        Users users =usersRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Email: " + email));

        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(UUID.randomUUID().toString());
        emailVerificationToken.setEmail(email);
        emailVerificationToken.setExpiryDate(Timestamp.from(Instant.now().plusSeconds(7 * 24 * 60 * 60)));

        emailVerificationRepogitory.save(emailVerificationToken);

        mailUtils.sendEmailVerificationMail(email, emailVerificationToken.getToken(), users.getFirstName()+" "+users.getLastName());
    }

    @Override
    public boolean validateToken(String token) {
        return emailVerificationRepogitory.findByToken(token)
                .filter(t -> t.getExpiryDate().toInstant().isAfter(Instant.now()))
                .isPresent();
    }

    @Override
    public void verifyEmail(String token) {
        // Fetch the email verification token from the repository
        EmailVerificationToken emailVerificationToken = emailVerificationRepogitory.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        // Check if the token has expired
        if (emailVerificationToken.getExpiryDate().toInstant().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        // Fetch the user associated with the email from the repository
        Users user = (Users) usersRepo.findByEmail(emailVerificationToken.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with the email: " + emailVerificationToken.getEmail()));

        // Update the user's verification status
        user.setVerified(true); // Corrected line
        usersRepo.save(user);

        emailVerificationRepogitory.delete(emailVerificationToken);
    }

    // Optional: Scheduled cleanup for expired tokens
    @Scheduled(fixedRate = 7 * 24 * 60 * 60)
    public void removeExpiredTokens() {
        List<EmailVerificationToken> expiredTokens = emailVerificationRepogitory.findAll()
                .stream()
                .filter(t -> t.getExpiryDate().toInstant().isBefore(Instant.now()))
                .collect(Collectors.toList());

        emailVerificationRepogitory.deleteAll(expiredTokens);
    }
}