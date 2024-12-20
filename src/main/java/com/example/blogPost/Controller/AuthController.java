package com.example.blogPost.Controller;

import com.example.blogPost.config.security.JwtHelper;
import com.example.blogPost.dto.PasswordResetFormDto;
import com.example.blogPost.jwt.JwtRequest;
import com.example.blogPost.jwt.JwtResponse;
import com.example.blogPost.model.Users;
import com.example.blogPost.repository.UsersRepo;
import com.example.blogPost.service.EmailVerificationService;
import com.example.blogPost.service.PasswordResetService;
import com.example.blogPost.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.UUID;

@RestController
@Controller
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


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users users) {
        try {
            userService.registerUser(users);
            emailVerificationService.createEmailVerificationToken(users.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body("User Successfully Created with Email : " + users.getEmail());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        JwtResponse response = userService.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException {
        passwordResetService.createPasswordResetToken(email);
        return new ResponseEntity<>("Password reset link has been sent to your email.", HttpStatus.OK);
    }

    @GetMapping("/reset-form")
    public ModelAndView resetForm(@RequestParam UUID token) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("token", token.toString());
        modelAndView.addObject("passwordResetForm", new PasswordResetFormDto()); // Add a form object for Thymeleaf
        modelAndView.setViewName("ResetPasswordForm");
        return modelAndView;
    }


    @PostMapping("/reset-password")
    public ModelAndView resetPassword(@RequestParam String token, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        passwordResetService.resetPassword(token, newPassword, confirmPassword);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("PasswordResetSuccess");
        return modelAndView;
    }

    @GetMapping("/verify-email")
    public ModelAndView verifyEmail(@RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView();
        emailVerificationService.verifyEmail(token);
        modelAndView.setViewName("email-verification-success");
        return modelAndView;
    }


    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(
            @RequestPart String oldPassword,
            @RequestPart String newPassword,
            @RequestPart String confirmPassword) {
        userService.changePassword(oldPassword, newPassword, confirmPassword);
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "status", required = false) Boolean status) {

        if (id != null) {
            return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
        }

        // Pass the status to the service method to filter users or get all users
        return new ResponseEntity<>(userService.getUsers(status), HttpStatus.OK);
    }


    @GetMapping("/user/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return new ResponseEntity<>(userService.getProfile(), HttpStatus.OK);
    }

}
