package com.msp.everestFitness.Controller;

import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.service.PaymentService;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    // Success URL handler for Stripe
    @GetMapping("/success")
    public ModelAndView handlePaymentSuccess(@RequestParam UUID orderId) throws ResourceNotFoundException, StripeException, MessagingException, IOException {

        paymentService.paymentSuccess(orderId);

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
