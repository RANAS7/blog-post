package com.example.blogPost.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Year;


@Service
public class MailUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${DOMAIN}")
    private String domain;

    String currentYear = String.valueOf(Year.now().getValue());

    //Mail configuration for Reset password
    public void sendPasswordResetEmail(String toEmail, String token, String recipientName) throws MessagingException {
        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String subject = "Reset Your Password with This Simple Click!";

            String resetLink = domain + "/api/auth/reset-form?token=" + token;


            ClassPathResource htmlFile = new ClassPathResource("templates/PasswordReset.html");
            String htmlContent = StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[RESET_LINK]", String.valueOf(resetLink))
                    .replace("[User]", recipientName)
                    .replace("[Year]", currentYear);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error:" + e.getMessage(), e);
        }

    }

    //Mail configuration for user verification
    public void sendEmailVerificationMail(String toEmail, String token, String recipientName) throws MessagingException, IOException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String subject = "Email Verification";

            // Build verification link
            String verificationLink = domain + "/api/auth/verify-email?token=" + token;

            // Read and process the HTML template
            ClassPathResource htmlFile = new ClassPathResource("templates/EmailVerification.html");
            String htmlContent = StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);

            // Replace placeholders in the HTML template
            htmlContent = htmlContent.replace("{verificationLink}", verificationLink);
            htmlContent = htmlContent.replace("{userName}", recipientName);
            htmlContent = htmlContent.replace("{currentYear}", currentYear);

            // Set email details
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Send email
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error: " + e.getMessage(), e);
        }
    }
}