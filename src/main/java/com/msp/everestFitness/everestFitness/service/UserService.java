package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.model.Users;

import java.util.List;
import java.util.UUID;

public interface UserService {
//    List<Users> getUsers();
    void registerUser(Users users);
    void changePassword(String oldPassword, String newPassword, String confirmPassword);
    List<Users> getAllUsers();
    Users getUserById(UUID id);
    Users getUserByEmail(String email);
    Users getUserByName(String name);
    List<Users> searchUsers(String name, String email);
    Users getByUserType(String userType);
}
