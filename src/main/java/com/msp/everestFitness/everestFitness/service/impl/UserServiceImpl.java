package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.config.LoginUtil;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.Users;
import com.msp.everestFitness.everestFitness.repository.UsersRepo;
import com.msp.everestFitness.everestFitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
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
        return usersRepo.findAll();
    }

    @Override
    public Users getUserById(UUID id) {
        return usersRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The user is not found with the id: " + id));
    }

    @Override
    public Users getUserByEmail(String email) {
        return (Users) usersRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("The user is not found with the email: " + email));
    }

//    @Override
//    public Users getUserByName(String name) {
//        return usersRepo.findByName(name);
//    }

    @Override
    public List<Users> searchUsers(String fName, String lName, String email) {
        if (fName != null || lName != null) {
            return usersRepo.findByFirstNameAndLastName(fName, lName);
        } else if (email != null && !email.isEmpty()) {
            return usersRepo.findByEmailIgnoreCase(email);
        } else {
            throw new IllegalArgumentException("Either name or email must be provided for the search");
        }
    }

    @Override
    public Users getByUserType(String userType) {
        return usersRepo.findByUserType(userType);
    }

}
