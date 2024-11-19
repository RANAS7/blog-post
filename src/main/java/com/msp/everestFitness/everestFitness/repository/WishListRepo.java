package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishListRepo extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByUsers_UserId(UUID currentUserId);
}
