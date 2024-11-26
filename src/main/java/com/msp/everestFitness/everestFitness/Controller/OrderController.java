package com.msp.everestFitness.everestFitness.Controller;

import com.msp.everestFitness.everestFitness.dto.GuestOrderRequest;
import com.msp.everestFitness.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.enumrated.PaymentMethod;
import com.msp.everestFitness.everestFitness.model.OrderItems;
import com.msp.everestFitness.everestFitness.model.Orders;
import com.msp.everestFitness.everestFitness.model.ShippingInfo;
import com.msp.everestFitness.everestFitness.service.OrderService;
import com.msp.everestFitness.everestFitness.service.PaymentService;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/")
    public ResponseEntity<?> createOrder(@RequestBody Orders order)
            throws MessagingException, IOException, StripeException {
        PaymentResponse response = orderService.createOrder(order);

        if (order.getPaymentMethod().equals(PaymentMethod.STRIPE)) {
            return new ResponseEntity<>(response.getPaymentUrl(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Order processing", HttpStatus.CREATED);
    }

    @PostMapping("/guest")
    public ResponseEntity<?> createGuestOrder(@RequestBody GuestOrderRequest orderRequest)
            throws StripeException, MessagingException, IOException {
        PaymentResponse response = orderService.createGuestOrder(orderRequest.getOrders(), orderRequest.getShippingInfo());
        if (orderRequest.getOrders().getPaymentMethod().equals(PaymentMethod.STRIPE)) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>("Order Processing", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getOrders(@RequestParam(required = false) UUID orderId) {
        if (orderId == null) {
            return new ResponseEntity<>(orderService.getAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(orderService.getById(orderId), HttpStatus.OK);
        }
    }

    @PostMapping("/status")
    public ResponseEntity<?> updateOrderStatus(@RequestParam UUID orderId, OrderStatus orderStatus) {
        orderService.updateOrderStatus(orderId, orderStatus);
        return new ResponseEntity<>("Order status updated successfully !!", HttpStatus.CREATED);
    }

    @GetMapping("/by-user")
    public ResponseEntity<?> getOrderOfUser() {
        return new ResponseEntity<>(orderService.getOrderOfUser(), HttpStatus.OK);
    }
}
