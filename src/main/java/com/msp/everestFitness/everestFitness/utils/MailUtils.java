package com.msp.everestFitness.everestFitness.utils;

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

            String verificationLink =domain + "/api/auth/verify-email?token=" + token;

            ClassPathResource htmlFile = new ClassPathResource("templates/EmailVerification.html");
            String htmlContent = StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[VERIFICATION_LINK]", verificationLink);
            htmlContent = htmlContent.replace("[User]", recipientName);
            htmlContent = htmlContent.replace("[Year]", currentYear);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error:" + e.getMessage(), e);
        }

    }


    public void sendKycApprovalEmail(String toEmail, String vendorName) throws MessagingException, IOException {
        try {
            // Create a MimeMessage object
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // Set the subject of the email
            String subject = "KYC Approval Notification";

            ClassPathResource htmlFile = new ClassPathResource("templates/kycApprovalNotice.html");
            String htmlContent = StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);

            String currentYear = String.valueOf(Year.now().getValue());

            // HTML content for the KYC approval notice
            htmlContent = htmlContent.replace("vendorName", vendorName);
            htmlContent = htmlContent.replace("currentYear", currentYear);

            // Set the content of the message
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Send the email
            javaMailSender.send(mimeMessage);
            System.out.println("KYC Approval Email sent successfully");
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw e; // Rethrow or handle accordingly
        }
    }


    public void sendKycRejectionEmail(String toEmail, String vendorName, String rejectionReason) throws MessagingException, IOException {
        try {
            // Create a MimeMessage object
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // Set the subject of the email
            String subject = "KYC Rejection Notification";

            ClassPathResource htmlFile = new ClassPathResource("templates/rejectionNotice.html");
            String htmlContent = StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);


            // HTML content for the KYC approval notice
            htmlContent = htmlContent.replace("vendorName", vendorName);
            htmlContent = htmlContent.replace("rejectionReason", rejectionReason);
            htmlContent = htmlContent.replace("currentYear", currentYear);

            // Set the content of the message
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Send the email
            javaMailSender.send(mimeMessage);
            System.out.println("KYC Approval Email sent successfully");
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw e; // Rethrow or handle accordingly
        }
    }
}
