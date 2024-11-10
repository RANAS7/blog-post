package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.OrderItems;
import com.msp.everestFitness.everestFitness.model.Orders;
import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Users;
import com.msp.everestFitness.everestFitness.repository.OrderItemsRepo;
import com.msp.everestFitness.everestFitness.repository.PaymentsRepo;
import com.msp.everestFitness.everestFitness.repository.ProductsRepo;
import com.msp.everestFitness.everestFitness.repository.UsersRepo;
import com.msp.everestFitness.everestFitness.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentsRepo paymentsRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private ProductsRepo productsRepo;

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @Autowired
    private OrderItemsRepo orderItemsRepo;

    @Override
    public PaymentResponse createPaymentLink(Orders savedOrder) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        // Ensure that ShippingInfo and Users are not null before proceeding
        if (savedOrder.getShippingInfo() == null) {
            throw new IllegalArgumentException("ShippingInfo is not available.");
        }

        Users user = usersRepo.findById(savedOrder.getShippingInfo().getUsers().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the id: " +
                        savedOrder.getShippingInfo().getUsers().getUserId()));

        // Create a new customer in Stripe with the provided email
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .build();
        Customer customer = Customer.create(customerParams);

//        Get order_items
        List<OrderItems> itemsList = orderItemsRepo.findByOrder_OrderId(savedOrder.getOrderId());

        // Create line items for the session
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        itemsList.forEach(item -> {

//            Get Product by ID
            Products products = productsRepo.findById(item.getProducts().getProductId()).get();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(Long.valueOf(item.getQuantity()))
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(BigDecimal.valueOf(item.getPrice())
                                            .multiply(BigDecimal.valueOf(100))
                                            .longValue())
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(products.getName()) // Ensured that a name is provided
                                                    .build())
                                    .build())
                    .build();
            lineItems.add(lineItem);
        });


        // Create session parameters
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8082/api/payment/success?orderId=" + savedOrder.getOrderId())
                .setCancelUrl("http://localhost:8082/api/payment/failed?orderId=" + savedOrder.getOrderId())
                .setCustomer(customer.getId())
                .addAllLineItem(lineItems)
                .build();

        // Create a checkout session
        Session session = Session.create(params);

        PaymentResponse response = new PaymentResponse();
        response.setPaymentUrl(session.getUrl());
        return response;
    }
}