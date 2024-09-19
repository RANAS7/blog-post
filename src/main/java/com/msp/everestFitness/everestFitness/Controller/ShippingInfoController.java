package com.msp.everestFitness.everestFitness.Controller;

import com.msp.everestFitness.everestFitness.model.ShippingInfo;
import com.msp.everestFitness.everestFitness.service.ShippingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        // Retrieve a single ShippingInfo by ID
        return new ResponseEntity<>(shippingInfoService.getShippingInfoById(id), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteShippingInfo(@RequestParam UUID id) {
        shippingInfoService.deleteShippingInfo(id);
        return new ResponseEntity<>("The Shipping Information Successfully Deletes", HttpStatus.OK);
    }

    @GetMapping("/by-user")
    public ResponseEntity<?> getShippingInfoByUsersUserId() {
        return new ResponseEntity<>(shippingInfoService.findByUsersUserId(), HttpStatus.OK);
    }

}
