package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.enumrated.PaymentMethod;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.helper.OrderHelper;
import com.msp.everestFitness.everestFitness.model.*;
import com.msp.everestFitness.everestFitness.repository.*;
import com.msp.everestFitness.everestFitness.service.OrderService;
import com.msp.everestFitness.everestFitness.utils.MailUtils;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private OrderItemsRepo orderItemsRepo;

    @Autowired
    private ShippingInfoRepo shippingInfoRepo;

    @Autowired
    private ProductsRepo productsRepo;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private CouponRepo couponRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private DeliveryOptRepo deliveryOptRepo;

    @Autowired
    private PaymentsRepo paymentsRepo;

    @Autowired
    private OrderHelper orderHelper;

    //    Create order for USER and MEMBER
    @Override
    public Orders createOrder(Orders order) throws ResourceNotFoundException, IOException, MessagingException, StripeException {
        UUID orderId = UUID.randomUUID();
        order.setOrderId(orderId);

        // Validate and fetch necessary order details
        ShippingInfo shippingInfo = orderHelper.fetchShippingInfo(order.getShippingInfo().getShippingId());
        Users user = orderHelper.fetchUser(shippingInfo.getUsers().getUserId());

        double total = orderHelper.calculateOrderTotal(order);
        orderHelper.validateMinimumOrderAmount(total);

        // Apply coupon if applicable
        double discountAmount = orderHelper.applyCoupon(order.getCoupon(), total);

        // Fetch delivery option and calculate delivery charge
        DeliveryOpt deliveryOpt = orderHelper.fetchDeliveryOption(order.getDeliveryOption());
        double deliveryCharge = deliveryOpt.getCharge();

        // Update order with fetched details
        order.setShippingInfo(shippingInfo);
        order.setTotal(total - discountAmount + deliveryCharge); // Adjust total for discount and delivery charge

        // Validate order ID
        if (order.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        return order; // Order is created and returned
    }


    //    Create order for GUEST
    @Override
    public void createGuestOrder(List<OrderItems> orderItems, ShippingInfo shippingInfo, String couponCode, String deliveryOpt)
            throws MessagingException, IOException, StripeException {

//        double total = orderHelper.calculateTotal(orderItems);
//
//        orderHelper.validateMinimumOrderAmount(total);
//
//        Users user = orderHelper.getOrCreateGuestUser(shippingInfo);
//
//        shippingInfo.setUsers(user);
//        ShippingInfo shippingInfo1 = shippingInfoRepo.save(shippingInfo);
//
//        double discountAmount = orderHelper.applyCoupon(couponCode, total);
//        double grandTotal = total - discountAmount;
//
//        // Apply delivery charge
//        DeliveryOpt deliveryOption = orderHelper.getDeliveryOption(deliveryOpt);
//        grandTotal += (double) deliveryOption.getCharge();
//
//        Orders orders = new Orders();
//        orders.setTotal(grandTotal);
//        orders.setShippingInfo(shippingInfo1);
//        Orders savedOrder = ordersRepo.save(orders);
//
//        orderHelper.saveOrderItems(orderItems, savedOrder);
//
//        // Process payment with Stripe
////        String paymentIntentId = orderHelper.createStripePaymentIntent(grandTotal, user.getEmail());
//
//        // Save payment information
////        orderHelper.savePaymentInfo(savedOrder, paymentIntentId, grandTotal);
//
//        mailUtils.sendOrderConfirmationMail(user.getEmail(), savedOrder.getOrderId());
//
////        return savedOrder;
    }


    @Override
    public Orders getOrderById(UUID orderId) {
        return ordersRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }


    @Override
    public List<Orders> getAllOrders() {
        return ordersRepo.findAll();
    }


    @Override
    public void deleteOrder(UUID orderId) {
        Orders existingOrder = getOrderById(orderId);
        ordersRepo.delete(existingOrder);
    }

    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus orderStatus) {
        // Additional logic to cancel the order
        Orders order = ordersRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        order.setOrderStatus(orderStatus);
        ordersRepo.save(order);

        if (orderStatus == OrderStatus.RETURNED || orderStatus == OrderStatus.CANCELLED) {
            List<OrderItems> orderItemsList = orderItemsRepo.findByOrder_OrderId(orderId);

            for (OrderItems orderItems : orderItemsList) {
                Products products = orderItems.getProducts();
                products.setStock(products.getStock() + orderItems.getQuantity());
                productsRepo.save(products);
            }
        }
    }
}
