package com.msp.everestFitness.everestFitness.Controller;

import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.enumrated.PaymentMethod;
import com.msp.everestFitness.everestFitness.enumrated.PaymentStatus;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.*;
import com.msp.everestFitness.everestFitness.repository.*;
import com.msp.everestFitness.everestFitness.service.OrderService;
import com.msp.everestFitness.everestFitness.utils.MailUtils;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentsRepo paymentsRepo;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private OrderItemsRepo orderItemsRepo;

    @Autowired
    private ProductsRepo productsRepo;


    // Success URL handler for Stripe
    @GetMapping("/success")
    public ModelAndView handlePaymentSuccess(@RequestParam UUID orderId) throws ResourceNotFoundException, StripeException, MessagingException, IOException {
        String paymentMethod = PaymentMethod.STRIPE.name();

        Payments payments = paymentsRepo.findByOrders_orderId(orderId);
        if (payments == null) {
            throw new ResourceNotFoundException("Payment record not found for order ID: " + orderId);
        }

        payments.setPaymentStatus(PaymentStatus.PAID);
        paymentsRepo.save(payments);

        Orders orders = ordersRepo.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found with the Id"));
        orders.setOrderStatus(OrderStatus.COMPLETED);
        ordersRepo.save(orders);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("orderId", orderId);
        modelAndView.setViewName("PaymentSuccessful");
        return modelAndView;
    }



    @GetMapping("/failed")
    public ModelAndView paymentFailed(@RequestParam UUID orderID) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "Payment was not successful. Please try again.");
        modelAndView.setViewName("PaymentFailed");
        return modelAndView;
    }
}
