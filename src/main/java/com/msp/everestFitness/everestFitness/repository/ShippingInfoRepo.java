package com.msp.everestFitness.everestFitness.repository;

import com.msp.everestFitness.everestFitness.enumrated.AddressType;
import com.msp.everestFitness.everestFitness.model.ShippingInfo;
import com.msp.everestFitness.everestFitness.service.ShippingInfoService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.Validate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShippingInfoRepo extends JpaRepository<ShippingInfo, UUID> {
    List<ShippingInfo> findByUsers_UserId(UUID userId);
    ShippingInfo findByUsersUserIdAndAddressType(UUID userId, AddressType addressType);
}
