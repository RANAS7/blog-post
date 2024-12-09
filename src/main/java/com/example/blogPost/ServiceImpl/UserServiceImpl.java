package com.example.blogPost.ServiceImpl;

import com.example.blogPost.config.LoginUtil;
import com.example.blogPost.enumrated.UserType;
import com.example.blogPost.exceptions.ResourceNotFoundException;
import com.example.blogPost.model.Users;
import com.example.blogPost.repository.UsersRepo;
import com.example.blogPost.service.UserService;
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
            if (user.getUserType().equals(UserType.ADMIN)) {
                continue;
            }
            users.add(user); // Corrected this line to add the user to 'users' list
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
    public List<Users> getByUserType(UserType userType) {
        List<Users> usersList = usersRepo.findByUserType(userType);
        List<Users> users = new ArrayList<>();
        for (Users user : usersList) {
            user.setPassword(null);
            users.add(user); // Corrected this line to add the user to 'users' list
        }
        return users;
    }

    @Override
    public Users getProfile() {
        Users user = usersRepo.findById(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the id: " + loginUtil.getCurrentUserId()));
        user.setPassword(null);
        return user;
    }
}
