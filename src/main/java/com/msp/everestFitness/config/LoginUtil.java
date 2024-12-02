package com.msp.everestFitness.config;

import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.model.Users;
import com.msp.everestFitness.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoginUtil {

    @Autowired
    private UsersRepo usersRepo;

    // Retrieve the current logged-in user's username
    public String getCurrentUserUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null; // If no authentication or anonymous user
    }

    // Retrieve the current logged-in user's details
    public UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null; // If no authentication or anonymous user
    }

    // Retrieve the current logged-in user's ID by querying the database
    public UUID getCurrentUserId() {
        String username = getCurrentUserUsername(); // Get the username from UserDetails
        if (username != null) {
            Users user = (Users) usersRepo.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with the email: " + username)); // Query the database by username/email
            if (user != null) {
                return user.getUserId();
            }
        }
        return null; // If no user is found
    }
}
