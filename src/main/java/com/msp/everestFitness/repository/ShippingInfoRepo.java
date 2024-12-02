package com.msp.everestFitness.repository;

import com.msp.everestFitness.enumrated.AddressType;
import com.msp.everestFitness.model.ShippingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShippingInfoRepo extends JpaRepository<ShippingInfo, UUID> {
    List<ShippingInfo> findByUsers_UserId(UUID userId);
    ShippingInfo findByUsersUserIdAndAddressType(UUID userId, AddressType addressType);
}
