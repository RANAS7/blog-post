package com.msp.everestFitness.service.impl;

import com.msp.everestFitness.config.LoginUtil;
import com.msp.everestFitness.enumrated.AddressType;
import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.model.ShippingInfo;
import com.msp.everestFitness.repository.ShippingInfoRepo;
import com.msp.everestFitness.service.ShippingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ShippingInfoServiceImpl implements ShippingInfoService {
    @Autowired
    private ShippingInfoRepo shippingInfoRepo;

    @Autowired
    private LoginUtil loginUtil;

    @Override
    public void addShippingInfo(ShippingInfo shippingInfo) {

        if (shippingInfo.getShippingId() != null) {
            ShippingInfo info = shippingInfoRepo.findById(shippingInfo.getShippingId())
                    .orElseThrow(() -> new ResourceNotFoundException("The Shipping Information is not exist in our record with the Id: " + shippingInfo.getShippingId()));

            // Check if the user is trying to change the PRIMARY address
            if (shippingInfo.getAddressType() == AddressType.PRIMARY && info.getAddressType() != AddressType.PRIMARY) {
                // Set the old PRIMARY address to ALTERNATIVE for this user
                ShippingInfo primaryAddress = shippingInfoRepo.findByUsersUserIdAndAddressType(shippingInfo.getUsers().getUserId(), AddressType.PRIMARY);
                if (primaryAddress != null) {
                    primaryAddress.setAddressType(AddressType.ALTERNATIVE);
                    shippingInfoRepo.save(primaryAddress);
                }
            }

            info.setAddress(shippingInfo.getAddress());
            info.setCity(shippingInfo.getCity());
            info.setState(shippingInfo.getState());
            info.setPostalCode(shippingInfo.getPostalCode());
            info.setAddressType(shippingInfo.getAddressType());
            info.setCountry(shippingInfo.getCountry());
            info.setUpdatedAt(Timestamp.from(Instant.now()));

            shippingInfoRepo.save(info);

        }

        // New ShippingInfo case

        // Check if the user already has a PRIMARY address
        ShippingInfo primaryAddress = shippingInfoRepo.findByUsersUserIdAndAddressType(shippingInfo.getUsers().getUserId(), AddressType.PRIMARY);
        if (primaryAddress == null) {
            // If no PRIMARY address exists, set the new one as PRIMARY
            shippingInfo.setAddressType(AddressType.PRIMARY);
        } else {
            // Otherwise, set the new address as ALTERNATIVE
            shippingInfo.setAddressType(AddressType.ALTERNATIVE);
        }

        shippingInfoRepo.save(shippingInfo);
    }


    @Override
    public ShippingInfo getShippingInfoById(UUID id) {
        ShippingInfo shippingInfo = shippingInfoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Shipping information with ID " + id + " does not exist."));
        shippingInfo.setUsers(null);
        return shippingInfo;
    }

    @Override
    public void deleteShippingInfo(UUID id) {
        if (id == null) {
            throw new ResourceNotFoundException("The Shipping Information is not exist in our record");
        }
        shippingInfoRepo.deleteById(id);
    }

    @Override
    public List<ShippingInfo> findByUsersUserId() {
        List<ShippingInfo> shippingInfoList = shippingInfoRepo.findByUsers_UserId(loginUtil.getCurrentUserId());

        List<ShippingInfo> infoList = new ArrayList<>();
        for (ShippingInfo info : shippingInfoList) {
            info.setUsers(null);
            infoList.add(info);
        }
        return infoList;
    }


}
