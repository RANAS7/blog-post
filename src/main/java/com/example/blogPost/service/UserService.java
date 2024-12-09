package com.example.blogPost.service;

import com.example.blogPost.enumrated.UserType;
import com.example.blogPost.model.Users;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void registerUser(Users users);
    void changePassword(String oldPassword, String newPassword, String confirmPassword);
    List<Users> getAllUsers();
    Users getUserById(UUID id);
    Users getUserByEmail(String email);
    List<Users> getByUserType(UserType userType);
    Users getProfile();
}
