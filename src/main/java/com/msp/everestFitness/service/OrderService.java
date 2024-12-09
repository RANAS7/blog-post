package com.msp.everestFitness.service;

import com.msp.everestFitness.dto.OrderDTO;
import com.msp.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.model.Orders;
import com.msp.everestFitness.model.ShippingInfo;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    PaymentResponse createOrder(Orders order) throws MessagingException, IOException, StripeException;
    OrderDTO getOrderById(UUID orderId);

    List<OrderDTO> getAllOrders();

    void deleteOrder(UUID orderId);

    void updateOrderStatus(UUID orderId, OrderStatus orderStatus);

    //    Create order for GUEST
    PaymentResponse createGuestOrder(Orders orders, ShippingInfo guestShippingInfo)
            throws ResourceNotFoundException, IOException, MessagingException, StripeException;

    List<OrderDTO> getOrderOfUser();
}