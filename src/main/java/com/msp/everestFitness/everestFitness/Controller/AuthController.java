package com.msp.everestFitness.everestFitness.Controller;

import com.msp.everestFitness.everestFitness.dto.PasswordResetFormDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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

        Users user = (Users) usersRepo.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("The user is not exist in our record with the email: " + request.getEmail()));

        this.doAuthenticate(request.getEmail(), request.getPassword());


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername())
                .userID(user.getUserId())
                .userType(user.getUserType().name())
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
//        return new ResponseEntity<>("Password has been reset successfully.", HttpStatus.OK);
    }

    @GetMapping("/verify-email")
    public ModelAndView verifyEmail(@RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView();
        emailVerificationService.verifyEmail(token);
        modelAndView.setViewName("email-verification-success");
        return modelAndView;
    }
//    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
//        emailVerificationService.verifyEmail(token);
//        return new ResponseEntity<>("Email verified successfully", HttpStatus.OK);


//    }


    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(@RequestParam UUID userId,
                                            @RequestParam String oldPassword,
                                            @RequestParam String newPassword,
                                            @RequestParam String confirmPassword) {
        userService.changePassword(userId, oldPassword, newPassword, confirmPassword);
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

    @GetMapping("/user/by-name")
    public ResponseEntity<?> getUserByName(@RequestParam String name) {
        return new ResponseEntity<>(userService.getUserByName(name), HttpStatus.OK);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<Users>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        return new ResponseEntity<>(userService.searchUsers(name, email), HttpStatus.OK);
    }

    @GetMapping("/users/type")
    public ResponseEntity<?> getUsersByUserType(@RequestParam String userType) {
        return new ResponseEntity<>(userService.getByUserType(userType), HttpStatus.OK);
    }

}
