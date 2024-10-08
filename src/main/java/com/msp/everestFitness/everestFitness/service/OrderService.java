package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.model.OrderItems;
import com.msp.everestFitness.everestFitness.model.Orders;
import com.msp.everestFitness.everestFitness.model.ShippingInfo;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    PaymentResponse createOrder(Orders order) throws MessagingException, IOException, StripeException;

    void createGuestOrder(List<OrderItems> orderItems, ShippingInfo shippingInfo, String couponCode, String deliveryOpt) throws MessagingException, IOException, StripeException;

    Orders getOrderById(UUID orderId);

    List<Orders> getAllOrders();

    void deleteOrder(UUID orderId);

    void updateOrderStatus(UUID orderId, OrderStatus orderStatus);
}
