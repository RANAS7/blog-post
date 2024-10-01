package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.model.DeliveryOpt;

import java.util.List;
import java.util.UUID;

public interface DeliveryOptService {
    // Create a new delivery option
    void createDeliveryOption(DeliveryOpt deliveryOption);

    // Get a delivery option by ID
    DeliveryOpt getDeliveryOptById(UUID optionId);

    // Get all delivery options
    List<DeliveryOpt> getAllDeliveryOptions();

    // Delete a delivery option
    void deleteDeliveryOption(UUID optionId);
}
