package com.msp.everestFitness.everestFitness.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @GetMapping("/success")
    public ModelAndView paymentSuccess(@RequestParam UUID orderId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("orderId", orderId);
        modelAndView.setViewName("PaymentSuccessful");
        return modelAndView;
    }

    @GetMapping("/failed")
    public ModelAndView paymentFailed() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("PaymentFailed");
        return modelAndView;
    }

}
