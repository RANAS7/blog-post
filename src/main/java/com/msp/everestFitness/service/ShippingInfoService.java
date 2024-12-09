package com.msp.everestFitness.service;

import com.msp.everestFitness.model.ShippingInfo;

import java.util.List;
import java.util.UUID;

public interface ShippingInfoService {
    void addShippingInfo(ShippingInfo shippingInfo);
//    List<ShippingInfo> getAllShippingInfo();
   ShippingInfo getShippingInfoById(UUID id);
   void deleteShippingInfo(UUID id);
    List<ShippingInfo> findByUsersUserId();
}