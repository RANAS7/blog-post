package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.Enumrated.UserType;
import com.msp.everestFitness.everestFitness.model.Users;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    List<Users> getUsers();
    Users registerUser(Users users);
}
