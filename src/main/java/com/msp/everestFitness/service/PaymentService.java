package com.msp.everestFitness.service;

import com.msp.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.model.Orders;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse createPaymentLink(Orders orders) throws StripeException;

    void paymentSuccess(UUID orderId) throws MessagingException, IOException;
}
