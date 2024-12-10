package com.example.blogPost.service;

import com.example.blogPost.enumrated.UserType;
import com.example.blogPost.jwt.JwtRequest;
import com.example.blogPost.jwt.JwtResponse;
import com.example.blogPost.model.Users;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void registerUser(Users users);
    void changePassword(String oldPassword, String newPassword, String confirmPassword);
    List<Users> getUsers(Boolean status);
    Users getUserById(UUID id);
    Users getUserByEmail(String email);
    Users getProfile();
    JwtResponse login(JwtRequest request);
}
