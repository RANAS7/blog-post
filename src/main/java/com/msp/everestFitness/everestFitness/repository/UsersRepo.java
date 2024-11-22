package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepo extends JpaRepository<Users, UUID> {

    Optional<Users> findByEmail(String email);

    // Custom query to find users by first and last name, allowing both to be null
    @Query("SELECT u FROM Users u WHERE " +
            "(:firstName IS NULL OR u.firstName = :firstName) AND " +
            "(:lastName IS NULL OR u.lastName = :lastName)")
    List<Users> findByFirstNameAndLastName(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName);

    List<Users> findByEmailIgnoreCase(String email);

    Users findByUserType(String userType);
}
