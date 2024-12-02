package com.msp.everestFitness.service;

import com.msp.everestFitness.enumrated.UserType;
import com.msp.everestFitness.model.Users;

import java.util.List;
import java.util.UUID;

public interface UserService {
//    List<Users> getUsers();
    void registerUser(Users users);
    void changePassword(String oldPassword, String newPassword, String confirmPassword);
    List<Users> getAllUsers();
    Users getUserById(UUID id);
    Users getUserByEmail(String email);
//    Users getUserByName(String name);

    List<Users> searchUsers(String fName, String lName, String email);

    List<Users> getByUserType(UserType userType);

    Users getProfile();
}
