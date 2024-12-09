package com.example.blogPost.Controller;

import com.example.blogPost.config.security.JwtHelper;
import com.example.blogPost.dto.PasswordResetFormDto;
import com.example.blogPost.enumrated.UserType;
import com.example.blogPost.exceptions.ResourceNotFoundException;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

        Users user =usersRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("The user is not exist in our record with the email: " + request.getEmail()));

        this.doAuthenticate(request.getEmail(), request.getPassword());


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
//                .username(userDetails.getUsername())
//                .userID(user.getUserId())
//                .userType(user.getUserType().name())
                .build();
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Clear the authentication from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // Optionally, you can perform additional actions like invalidating the token in your storage if needed.
            SecurityContextHolder.clearContext(); // Clear the context for the current user
        }

        // Return a success response
        return new ResponseEntity<>("Successfully logged out", HttpStatus.OK);
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
    public ResponseEntity<?> getUsers(@RequestParam(name = "id", required = false) UUID id) {
        if (id != null) {
            return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/user/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }


    @GetMapping("/users/type")
    public ResponseEntity<?> getUsersByUserType(@RequestParam UserType userType) {
        return new ResponseEntity<>(userService.getByUserType(userType), HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return new ResponseEntity<>(userService.getProfile(), HttpStatus.OK);
    }

}
