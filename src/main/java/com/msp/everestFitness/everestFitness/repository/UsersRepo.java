package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepo extends JpaRepository<Users, UUID> {
    Optional<Object> findByEmail(String email);
    Users findByName(String name);
    List<Users> findByNameContainingIgnoreCase(String name);
    List<Users> findByEmailIgnoreCase(String email);
    Users findByUserType(String userType);
}
