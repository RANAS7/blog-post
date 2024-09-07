package com.msp.everestFitness.everestFitness.controller;

import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.jwt.JwtRequest;
import com.msp.everestFitness.everestFitness.jwt.JwtResponse;
import com.msp.everestFitness.everestFitness.config.security.JwtHelper;
import com.msp.everestFitness.everestFitness.model.Users;
import com.msp.everestFitness.everestFitness.repository.UsersRepo;
import com.msp.everestFitness.everestFitness.service.EmailVerificationService;
import com.msp.everestFitness.everestFitness.service.PasswordResetService;
import com.msp.everestFitness.everestFitness.service.UserService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UserService userService;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailVerificationService emailVerificationService;


    @Autowired
    private JwtHelper helper;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users users) {
        try {
            userService.registerUser(users);
            emailVerificationService.createEmailVerificaionToken(users.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body("User Successfully Created with Email : " + users.getEmail());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {

        Users user = (Users) usersRepo.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("The user is not exist in our record with the email: " + request.getEmail()));

        this.doAuthenticate(request.getEmail(), request.getPassword());


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername())
                .userID(user.getUserId()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }

    }

    @GetMapping("/current_user")
    public String getLogInUser(Principal principal) {
        return principal.getName();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException {
        passwordResetService.createPasswordResetToken(email);
        return new ResponseEntity<>("Password reset link has been sent to your email.", HttpStatus.OK);
    }

//@GetMapping("/reset-form")
//public String showResetPasswordForm(@RequestParam(name = "token", required = false) String token, Model model) {
//    PasswordResetFormDto passwordResetForm = new PasswordResetFormDto(); // Create an instance of PasswordResetForm
//    model.addText("passwordResetForm");
//    model.addText("token");
//    return "ResetPasswordForm";
//}


    @GetMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        passwordResetService.resetPassword(token, newPassword, confirmPassword);
        return new ResponseEntity<>("Password has been reset successfully.", HttpStatus.OK);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        emailVerificationService.verifyEmail(token);
        return new ResponseEntity<>("Email verified successfully", HttpStatus.OK);
    }
}
