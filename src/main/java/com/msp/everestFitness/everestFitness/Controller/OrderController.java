package com.msp.everestFitness.everestFitness.Controller;

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

        Orders createdOrder = orderService.createOrder(order);
        PaymentResponse response = paymentService.createPaymentLink(createdOrder);

        return new ResponseEntity<>("Order created successfully: " + response.getPaymentUrl(), HttpStatus.CREATED);
    }

    @PostMapping("/guest")
    public ResponseEntity<?> createGuestOrder(
            @RequestBody List<OrderItems> orderItems,
            @RequestBody ShippingInfo shippingInfo,
            @RequestParam(required = false) String couponCode,
            @RequestParam String deliveryOpt)
            throws MessagingException, IOException, StripeException {
        orderService.createGuestOrder(orderItems, shippingInfo, couponCode, deliveryOpt);
        return new ResponseEntity<>("Order created successfully! Please check your email for confirmation.", HttpStatus.CREATED);

    }

    @PostMapping("/status")
    public ResponseEntity<?> updateOrderStatus(@RequestParam UUID orderId, OrderStatus orderStatus) {
        orderService.updateOrderStatus(orderId, orderStatus);
        return new ResponseEntity<>("Order status updated successfully !!", HttpStatus.CREATED);
    }
}
