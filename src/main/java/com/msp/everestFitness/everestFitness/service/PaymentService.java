package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.everestFitness.model.Orders;
import com.stripe.exception.StripeException;

public interface PaymentService {
    PaymentResponse createPaymentLink(Orders orders) throws StripeException;
}
