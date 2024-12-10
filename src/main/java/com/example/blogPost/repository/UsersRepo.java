package com.example.blogPost.repository;

import com.example.blogPost.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    List<Users> findByIsVerifiedTrue();
    List<Users> findByIsVerifiedFalse();
}
