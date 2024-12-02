package com.msp.everestFitness.Controller;

import com.msp.everestFitness.dto.GuestOrderRequest;
import com.msp.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.enumrated.PaymentMethod;
import com.msp.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.model.Orders;
import com.msp.everestFitness.service.OrderService;
import com.msp.everestFitness.service.PaymentService;
import com.stripe.exception.StripeException;
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
        if (orderId != null) {
            return new ResponseEntity<>(orderService.getOrderById(orderId), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
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
