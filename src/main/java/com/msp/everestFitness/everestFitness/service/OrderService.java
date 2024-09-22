package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.model.Orders;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    void createOrder(Orders order, String couponCode) throws MessagingException, IOException;

    void createGuestOrder(Orders order, String couponCode) throws MessagingException, IOException;

    Orders getOrderById(UUID orderId);

    List<Orders> getAllOrders();

    void deleteOrder(UUID orderId);

    void updateOrderStatus(UUID orderId, OrderStatus orderStatus);
}
