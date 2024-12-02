package com.msp.everestFitness.service.impl;

import com.msp.everestFitness.config.LoginUtil;
import com.msp.everestFitness.enumrated.UserType;
import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.model.Users;
import com.msp.everestFitness.repository.UsersRepo;
import com.msp.everestFitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void registerUser(Users users) {
        if (users.getPassword() == null) {
            throw new IllegalArgumentException("Password required");
        }
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
    public List<Users> getAllUsers() {
        List<Users> usersList = usersRepo.findAll();
        List<Users> users = new ArrayList<>();
        for (Users user : usersList) {
            user.setPassword(null);

            usersList.add(user);
        }

        return usersList;
    }

    @Override
    public Users getUserById(UUID id) {
        Users users = usersRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The user is not found with the id: " + id));
        users.setPassword(null);
        return users;
    }

    @Override
    public Users getUserByEmail(String email) {
        Users users = usersRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("The user is not found with the email: " + email));
        users.setPassword(null);
        return users;
    }

    @Override
    public List<Users> searchUsers(String fName, String lName, String email) {
        if (fName != null || lName != null) {
            List<Users> usersList = usersRepo.findByFirstNameAndLastName(fName, lName);

            List<Users> users = new ArrayList<>();
            for (Users user : usersList) {
                user.setPassword(null);

                usersList.add(user);
            }

            return usersList;
        } else if (email != null && !email.isEmpty()) {
            List<Users> usersList = usersRepo.findByEmailIgnoreCase(email);
            List<Users> users = new ArrayList<>();
            for (Users user : usersList) {
                user.setPassword(null);

                usersList.add(user);
            }

            return usersList;
        } else {
            throw new IllegalArgumentException("Either name or email must be provided for the search");
        }
    }

    @Override
    public List<Users> getByUserType(UserType userType) {
        List<Users> usersList = usersRepo.findByUserType(userType);

        List<Users> users = new ArrayList<>();
        for (Users user : usersList) {
            user.setPassword(null);

            usersList.add(user);
        }

        return usersList;
    }

    @Override
    public Users getProfile() {
        Users users = usersRepo.findById(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the id: " + loginUtil.getCurrentUserId()));
        users.setPassword(null);
        return users;
    }

}
