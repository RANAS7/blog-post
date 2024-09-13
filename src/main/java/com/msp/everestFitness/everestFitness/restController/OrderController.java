package com.msp.everestFitness.everestFitness.restController;

import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.model.Orders;
import com.msp.everestFitness.everestFitness.service.OrderService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

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

    @PostMapping("/status")
    public ResponseEntity<?> updateOrderStatus(@RequestParam UUID orderId, OrderStatus orderStatus) {
        orderService.updateOrderStatus(orderId, orderStatus);
        return new ResponseEntity<>("Order status updated successfully !!", HttpStatus.CREATED);
    }
}
