package com.example.blogPost.ServiceImpl;

import com.example.blogPost.config.LoginUtil;
import com.example.blogPost.config.security.JwtHelper;
import com.example.blogPost.enumrated.UserType;
import com.example.blogPost.exceptions.ResourceNotFoundException;
import com.example.blogPost.jwt.JwtRequest;
import com.example.blogPost.jwt.JwtResponse;
import com.example.blogPost.model.Users;
import com.example.blogPost.repository.UsersRepo;
import com.example.blogPost.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginUtil loginUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtHelper helper;

    @Override
    public void registerUser(Users users) {
        if (users.getPassword() == null) {
            throw new IllegalArgumentException("Password required");
        }
        users.setVerified(false);
        users.setCreatedAt(Timestamp.from(Instant.now()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        usersRepo.save(users);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String confirmPassword) {
        Users user = usersRepo.findById(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepo.save(user);
    }

    @Override
    public List<Users> getUsers(Boolean status) {
        List<Users> users = new ArrayList<>();

        // Fetch users based on the verification status or get all users if status is null
        List<Users> usersList;
        if (status == null) {
            usersList = usersRepo.findAll(); // Get all users if status is null
        } else if (status) {
            usersList = usersRepo.findByIsVerifiedTrue(); // Get only verified users if status is true
        } else {
            usersList = usersRepo.findByIsVerifiedFalse(); // Get only non-verified users if status is false
        }

        // Iterate through the users and process them
        for (Users user : usersList) {
            user.setPassword(null); // Remove the password for security reasons
            if (user.getUserType().equals(UserType.ADMIN)) {
                continue; // Skip the users with 'ADMIN' user type
            }
            users.add(user); // Add the user to the 'users' list
        }

        return users;
    }


    @Override
    public Users getUserById(UUID id) {
        Users user = usersRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The user is not found with the id: " + id));
        user.setPassword(null);
        return user;
    }

    @Override
    public Users getUserByEmail(String email) {
        Users user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("The user is not found with the email: " + email));
        user.setPassword(null);
        return user;
    }


    @Override
    public Users getProfile() {
        Users user = usersRepo.findById(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the id: " + loginUtil.getCurrentUserId()));
        user.setPassword(null);
        return user;
    }

    @Override
    public JwtResponse login(JwtRequest request) {
        Users user = usersRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("The user is not found in our records with the email: " + request.getEmail()));

        // Check if the user is verified before proceeding with authentication
        if (!user.isVerified()){            throw new IllegalArgumentException("Please verify your email before logging in.");
        }else {
            // If user is verified, proceed with authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
                try {
                    manager.authenticate(authentication); // Authenticate the user
                } catch (BadCredentialsException e) {
                    throw new BadCredentialsException("Invalid Username or Password!!");
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
                String token = this.helper.generateToken(userDetails);

                JwtResponse response = JwtResponse.builder()
                        .jwtToken(token)
                        .build();

                return response; // Return JwtResponse directly
            }

    }
}
