package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.model.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
//    List<Users> getUsers();
    Users registerUser(Users users);
    boolean changePassword();

}
