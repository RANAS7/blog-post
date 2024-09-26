package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.model.Carts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepo extends JpaRepository<Carts, UUID> {
    @Query(value = "SELECT * FROM carts WHERE user_id = :userId AND is_active = :isActive", nativeQuery = true)
    Optional<Carts> findByUserIdAndIsActive(@Param("userId") UUID userId, @Param("isActive") boolean isActive);

    Carts findByUsers_UserId(UUID users);
}
