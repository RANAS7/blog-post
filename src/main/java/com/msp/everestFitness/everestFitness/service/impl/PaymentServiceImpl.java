package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.everestFitness.model.OrderItems;
import com.msp.everestFitness.everestFitness.model.Orders;
import com.msp.everestFitness.everestFitness.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @Override
    public PaymentResponse createPaymentLink(Orders orders) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        // Create a new customer in Stripe with the provided email
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setEmail(orders.getShippingInfo().getUsers().getEmail()) // Set the customer's email here
                .build();
        Customer customer = Customer.create(customerParams); // Create the customer

        double totalAmt = 0.0;

        for (OrderItems item : orders.getOrderItems()) {
            totalAmt += item.getQuantity() * item.getPrice();
        }



        // Create session parameters
        SessionCreateParams params = SessionCreateParams.builder()
                .addAllPaymentMethodType(Collections.singletonList(SessionCreateParams.PaymentMethodType.CARD))
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8082/api/payment/success?orderId=" + orders.getOrderId())
                .setCancelUrl("http://localhost:8082/api/payment/failed")
                .setCustomer(customer.getId())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()

                                .setCurrency("usd")
                                .setUnitAmountDecimal(BigDecimal.valueOf(totalAmt))
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Order #" + orders.getOrderId())
                                        .build())
                                .build())
                        .build())
                .build(); // Don't forget to build the params!

        // Create a checkout session
        Session session = Session.create(params);
        PaymentResponse response = new PaymentResponse();
        response.setPaymentUrl(session.getUrl());
        return response; // Return the checkout session URL
    }
}
