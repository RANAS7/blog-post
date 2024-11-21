package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.enumrated.PaymentStatus;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.*;
import com.msp.everestFitness.everestFitness.repository.*;
import com.msp.everestFitness.everestFitness.service.PaymentService;
import com.msp.everestFitness.everestFitness.utils.MailUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Autowired
    private DeliveryOptRepo deliveryOptRepo;

    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private ShippingInfoRepo shippingInfoRepo;


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

//        Get deliver option
        DeliveryOpt deliveryOpt = deliveryOptRepo.findById(savedOrder.getDeliveryOpt().getOptionId()).get();

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
                                    .setUnitAmountDecimal(
                                            BigDecimal.valueOf(item.getPrice() * 100)
                                    )
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(products.getName()) // Ensured that a name is provided
                                                    .build())
                                    .build())
                    .build();
            lineItems.add(lineItem);
        });

        // Adding a delivery charge as a separate line item
        SessionCreateParams.LineItem deliveryChargeItem = SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmountDecimal(BigDecimal.valueOf(deliveryOpt.getCharge() * 100)) // Corrected the parentheses
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("Delivery Charge")
                                                .build())
                                .build())
                .build();
        lineItems.add(deliveryChargeItem);


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


    @Override
    public void paymentSuccess(UUID orderId) throws MessagingException, IOException {
        // Fetch payment record
        Payments payments = paymentsRepo.findByOrders_orderId(orderId);
        if (payments == null) {
            throw new ResourceNotFoundException("Payment record not found for order ID: " + orderId);
        }

        // Update payment status
        payments.setPaymentStatus(PaymentStatus.PAID);
        paymentsRepo.save(payments);

        // Fetch and update order status
        Orders orders = ordersRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with the ID: " + orderId));
        orders.setOrderStatus(OrderStatus.COMPLETED);
        ordersRepo.save(orders);

        // Update product stock based on order items
        List<OrderItems> orderItemsList = orderItemsRepo.findByOrder_OrderId(orders.getOrderId());
        for (OrderItems items : orderItemsList) {
            Products products = productsRepo.findById(items.getProducts().getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with the ID: " + items.getProducts().getProductId()));

            if (products.getStock() < items.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product ID: " + products.getProductId());
            }

            products.setStock(products.getStock() - items.getQuantity());
            productsRepo.save(products);
        }

        // Clear user cart
        Carts carts = cartRepo.findByUsers_UserId(orders.getShippingInfo().getUsers().getUserId());
        if (carts != null) {
            List<CartItems> cartItemsList = cartItemRepo.findByCarts_cartId(carts.getCartId());
            for (CartItems item : cartItemsList) {
                cartItemRepo.deleteById(item.getCartItemId());
            }
        }

        ShippingInfo shippingInfo = shippingInfoRepo.findById(orders.getShippingInfo().getShippingId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipping info not found with the id: " + orders.getShippingInfo().getShippingId()));

        Users users = usersRepo.findById(shippingInfo.getUsers().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the id: " + shippingInfo.getUsers().getUserId()));


        mailUtils.sendOrderConfirmationMail(users.getEmail(), orders);

    }

}