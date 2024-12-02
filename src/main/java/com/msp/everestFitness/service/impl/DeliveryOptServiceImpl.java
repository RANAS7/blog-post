package com.msp.everestFitness.service.impl;

import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.model.DeliveryOpt;
import com.msp.everestFitness.repository.DeliveryOptRepo;
import com.msp.everestFitness.service.DeliveryOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DeliveryOptServiceImpl implements DeliveryOptService {
    @Autowired
    private DeliveryOptRepo deliveryOptRepo;


    // Create a new delivery option
    @Override
    public void createDeliveryOption(DeliveryOpt deliveryOption) {
        if (deliveryOption.getOptionId() != null) {
            DeliveryOpt deliveryOpt = deliveryOptRepo.findById(deliveryOption.getOptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Delivery option not found with the Id: " + deliveryOption.getOptionId()));

            deliveryOpt.setOption(deliveryOption.getOption());
            deliveryOpt.setDescription(deliveryOpt.getDescription());
            deliveryOpt.setCharge(deliveryOption.getCharge());
            deliveryOpt.setUpdated(Timestamp.from(Instant.now()));
            deliveryOptRepo.save(deliveryOpt);
        }
        deliveryOption.setCharge(deliveryOption.getCharge());
        deliveryOptRepo.save(deliveryOption);
    }

    // Get a delivery option by ID
    @Override
    public DeliveryOpt getDeliveryOptById(UUID optionId) {
        return deliveryOptRepo.findById(optionId).orElseThrow(()-> new ResourceNotFoundException("Delivery option not found with the Id: " +optionId));
    }

    // Get all delivery options
    @Override
    public List<DeliveryOpt> getAllDeliveryOptions() {
        return deliveryOptRepo.findAll();
    }

    // Delete a delivery option
    @Override
    public void deleteDeliveryOption(UUID optionId) {
        deliveryOptRepo.deleteById(optionId);
    }
}
