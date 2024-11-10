package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.OrderItems;
import com.msp.everestFitness.everestFitness.model.Orders;
import com.msp.everestFitness.everestFitness.model.Users;
import com.msp.everestFitness.everestFitness.repository.PaymentsRepo;
import com.msp.everestFitness.everestFitness.repository.UsersRepo;
import com.msp.everestFitness.everestFitness.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentsRepo paymentsRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @Override
    public PaymentResponse createPaymentLink(Orders savedOrder) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        System.out.println("Orders is : " + savedOrder);


        // Ensure that ShippingInfo and Users are not null before proceeding
        if (savedOrder.getShippingInfo() == null) {
            throw new IllegalArgumentException("ShippingInfo is not available.");
        }

        Users user = usersRepo.findById(savedOrder.getShippingInfo().getUsers().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the id: " + savedOrder.getShippingInfo().getUsers().getUserId()));

        BigDecimal total= BigDecimal.valueOf(savedOrder.getTotal());

        // Convert total amount to cents while keeping two decimal places and convert to integer
        BigDecimal totalAmountInCents = total.multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        int unitAmountCents = totalAmountInCents.intValue(); // Convert to integer for Stripe

        // Create a new customer in Stripe with the provided email
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .build();
        Customer customer = Customer.create(customerParams);

        // Create session parameters
        SessionCreateParams params = SessionCreateParams.builder()
                .addAllPaymentMethodType(Collections.singletonList(SessionCreateParams.PaymentMethodType.CARD))
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8082/api/payment/success?orderId=" + savedOrder.getOrderId())
                .setCancelUrl("http://localhost:8082/api/payment/failed?orderId=" + savedOrder.getOrderId())
                .setCustomer(customer.getId())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
//                                .setUnitAmountDecimal(BigDecimal.valueOf(savedOrder.getTotal() * 100))
                                .setUnitAmountDecimal(BigDecimal.valueOf(unitAmountCents))
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Order Id: " + savedOrder.getOrderId())
                                        .build())
                                .build())
                        .build())
                .build();

        // Create a checkout session
        Session session = Session.create(params);
        PaymentResponse response = new PaymentResponse();
        response.setPaymentUrl(session.getUrl());
        return response;
    }
}