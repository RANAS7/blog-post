package com.msp.everestFitness.everestFitness.controller;

import com.msp.everestFitness.everestFitness.model.ShippingInfo;
import com.msp.everestFitness.everestFitness.service.ShippingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/shipping/info")
public class ShippingInfoController {
    @Autowired
    private ShippingInfoService shippingInfoService;

    @PostMapping("/")
    public ResponseEntity<?> addShippingInfo(@RequestBody ShippingInfo shippingInfo) {
        shippingInfoService.addShippingInfo(shippingInfo);
        return new ResponseEntity<>("Your shipping information successfully added", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getShippingInfo(@RequestParam(name = "id", required = false) UUID id) {
        if (id != null) {
            // Retrieve a single ShippingInfo by ID
            Optional<ShippingInfo> shippingInfo = Optional.ofNullable(shippingInfoService.getShippingInfoById(id));
            if (shippingInfo.isPresent()) {
                return new ResponseEntity<>(shippingInfo.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("ShippingInfo not found with ID: " + id, HttpStatus.NOT_FOUND);
            }
        } else {
            // Retrieve all ShippingInfo records
            List<ShippingInfo> shippingInfos = shippingInfoService.getAllShippingInfo();
            return new ResponseEntity<>(shippingInfos, HttpStatus.OK);
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteShippingInfo(@RequestParam UUID id) {
        shippingInfoService.deleteShippingInfo(id);
        return new ResponseEntity<>("The Shipping Information Successfully Deletes", HttpStatus.OK);
    }

}
