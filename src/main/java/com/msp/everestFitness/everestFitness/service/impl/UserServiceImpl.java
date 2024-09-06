package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.enumrated.UserType;
import com.msp.everestFitness.everestFitness.model.Users;
import com.msp.everestFitness.everestFitness.repository.UsersRepo;
import com.msp.everestFitness.everestFitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


//    private static final List<Users> store = new ArrayList<>();
//
//    public void UserService() {
//        store.add(new Users(UUID.randomUUID(), "deepak.rana@example.com", "password123", UserType.ADMIN));
//        store.add(new Users(UUID.randomUUID(), "john.doe@example.com", "password123", UserType.MEMBER));
//        store.add(new Users(UUID.randomUUID(), "jane.doe@example.com", "password123", UserType.MEMBER));
//        store.add(new Users(UUID.randomUUID(), "alice.smith@example.com", "password123", UserType.ADMIN));
//        store.add(new Users(UUID.randomUUID(), "bob.jones@example.com", "password123", UserType.MEMBER));
//    }
//
//    @Override
//    public List<Users> getUsers() {
//        return store;
//    }

    @Override
    public Users registerUser(Users users) {
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        return usersRepo.save(users);
    }

    @Override
    public boolean changePassword() {
        return false;
    }
}
