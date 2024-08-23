package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.Enumrated.UserType;
import com.msp.everestFitness.everestFitness.model.Users;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private List<Users> store = new ArrayList<>();

    public UserService() {
        store.add(new Users(UUID.randomUUID(), "deepak.rana@example.com", "password123", UserType.ADMIN));
        store.add(new Users(UUID.randomUUID(), "john.doe@example.com", "password123", UserType.MEMBER));
        store.add(new Users(UUID.randomUUID(), "jane.doe@example.com", "password123", UserType.MEMBER));
        store.add(new Users(UUID.randomUUID(), "alice.smith@example.com", "password123", UserType.ADMIN));
        store.add(new Users(UUID.randomUUID(), "bob.jones@example.com", "password123", UserType.MEMBER));
    }

    public List<Users> getUsers(){
        return this.store;
    }
}
