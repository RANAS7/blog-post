package com.msp.everestFitness.everestFitness.controller;

import com.msp.everestFitness.everestFitness.model.Users;
import com.msp.everestFitness.everestFitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public List<Users> getUser(){
        return userService.getUsers();
    }

    @GetMapping("/current_user")
    public String getLogInUser(Principal principal){
        return principal.getName();
    }
}
