package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.everestFitness.model.Orders;
import com.msp.everestFitness.everestFitness.model.Payments;
import com.stripe.exception.StripeException;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse createPaymentLink(Orders orders) throws StripeException;
}
