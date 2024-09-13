//package com.msp.everestFitness.everestFitness.controller;
//
//import com.msp.everestFitness.everestFitness.service.EmailVerificationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequestMapping("/api/auth")
//public class EmailVerificationController { // Corrected controller class name
//
//    @Autowired
//    private EmailVerificationService emailVerificationService;
//
//    @GetMapping("/verify-email")
//    public String verifyEmail(@RequestParam String token, Model model) {
//        boolean isVerified = emailVerificationService.verifyEmail(token);
//
//        if (isVerified) {
//            // Set model attributes to pass data to the success template
//            model.addAttribute("message", "Your email has been verified successfully!");
//
//            // Return the Thymeleaf template for a successful email verification
//            return "email-verification-success";
//        } else {
//            // Set error message and return the failure template for invalid or expired tokens
//            model.addAttribute("error", "Invalid or expired verification token.");
//            return "email-verification-failed";
//        }
//    }
//}
