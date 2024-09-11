package com.msp.everestFitness.everestFitness.controller;

import com.msp.everestFitness.everestFitness.model.Orders;
import com.msp.everestFitness.everestFitness.service.OrderService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<?> createOrder(@RequestBody Orders orders) throws MessagingException, IOException {
        orderService.createOrder(orders);
        return new ResponseEntity<>("Order created successfully please checkout you email", HttpStatus.CREATED);
    }

    @PostMapping("/guest")
    public ResponseEntity<?> createGuestOrder(@RequestBody Orders orders) throws MessagingException, IOException {
        orderService.createGuestOrder(orders);
        return new ResponseEntity<>("Order created successfully please checkout your email", HttpStatus.CREATED);
    }

}
