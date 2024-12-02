package com.msp.everestFitness.Controller;

import com.msp.everestFitness.model.DeliveryOpt;
import com.msp.everestFitness.service.DeliveryOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryOptController {

    @Autowired
    private DeliveryOptService deliveryOptService;


    @PostMapping("/")
    public ResponseEntity<?> createDeliveryOption(@RequestBody DeliveryOpt deliveryOpt) {
        deliveryOptService.createDeliveryOption(deliveryOpt);
        return new ResponseEntity<>("Delivery option created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getDeliveryOptionById(@RequestParam(required = false) UUID optionId) {
        if (optionId == null) {
            return new ResponseEntity<>(deliveryOptService.getAllDeliveryOptions(), HttpStatus.OK);
        }
        return new ResponseEntity<>(deliveryOptService.getDeliveryOptById(optionId), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteDeliveryOption(@RequestParam UUID optionId) {
        deliveryOptService.deleteDeliveryOption(optionId);
        return new ResponseEntity<>("Delivery option deleted successfully", HttpStatus.NO_CONTENT);
    }
}
