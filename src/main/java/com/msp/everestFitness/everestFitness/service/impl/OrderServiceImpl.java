package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.hepler.OrderHelper;
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
    private OrderHelper orderHelper;


    //    Create order for USER and MEMBER
    @Override
    public void createOrder(List<OrderItems> orderItems, UUID shippingInfoId, String couponCode, String deliveryOpt)
            throws ResourceNotFoundException, IOException, MessagingException, StripeException {

        // Fetch the ShippingInfo
        ShippingInfo shippingInfo = shippingInfoRepo.findById(shippingInfoId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping info not found"));

        // Fetch the User
        Users user = usersRepo.findById(shippingInfo.getUsers().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the Id: " + shippingInfo.getUsers().getUserId()));

        // Calculate total
        double total = orderHelper.calculateTotal(orderItems);

        // Validate minimum order amount
        orderHelper.validateMinimumOrderAmount(total);

        // Create new order
        Orders order = new Orders();
        order.setShippingInfo(shippingInfo);

        // Apply coupon if provided
        double discountAmount = 0.0;
        if (couponCode != null && !couponCode.isEmpty()) {
            discountAmount = orderHelper.applyCoupon(couponCode, total);
        }

        // Calculate grand total after discount
        double grandTotal = total - discountAmount;

        // Apply delivery charge
        DeliveryOpt deliveryOption = orderHelper.getDeliveryOption(deliveryOpt);
//        if (deliveryOption != null) {
//            grandTotal += deliveryOption.getCharge();
//            order.setDeliveryOpt(deliveryOption);
//        } else {
//            throw new IllegalArgumentException("Invalid delivery option");
//        }

        order.setTotal(grandTotal);

        // Save the order
        Orders savedOrder = ordersRepo.save(order);

        // Save order items and update product stock
        orderHelper.saveOrderItems(orderItems, savedOrder);

        // Process payment with Stripe
        String paymentIntentId = orderHelper.createStripePaymentIntent(grandTotal, user.getEmail());

        // Save payment information
        orderHelper.savePaymentInfo(savedOrder, paymentIntentId, grandTotal);

        // Send confirmation email
        mailUtils.sendOrderConfirmationMail(user.getEmail(), savedOrder.getOrderId());

        // Clear user's cart
        orderHelper.clearUserCart(user.getUserId());

//        return savedOrder;
    }


    //    Create order for GUEST
    @Override
    public void createGuestOrder(List<OrderItems> orderItems, ShippingInfo shippingInfo, String couponCode, String deliveryOpt)
            throws MessagingException, IOException, StripeException {

        double total = orderHelper.calculateTotal(orderItems);

        orderHelper.validateMinimumOrderAmount(total);

        Users user = orderHelper.getOrCreateGuestUser(shippingInfo);

        shippingInfo.setUsers(user);
        ShippingInfo shippingInfo1 = shippingInfoRepo.save(shippingInfo);

        double discountAmount = orderHelper.applyCoupon(couponCode, total);
        double grandTotal = total - discountAmount;

        // Apply delivery charge
        DeliveryOpt deliveryOption = orderHelper.getDeliveryOption(deliveryOpt);
        grandTotal += (double) deliveryOption.getCharge();

        Orders orders = new Orders();
        orders.setTotal(grandTotal);
        orders.setShippingInfo(shippingInfo1);
        Orders savedOrder = ordersRepo.save(orders);

        orderHelper.saveOrderItems(orderItems, savedOrder);

        // Process payment with Stripe
        String paymentIntentId = orderHelper.createStripePaymentIntent(grandTotal, user.getEmail());

        // Save payment information
        orderHelper.savePaymentInfo(savedOrder, paymentIntentId, grandTotal);

        mailUtils.sendOrderConfirmationMail(user.getEmail(), savedOrder.getOrderId());

//        return savedOrder;
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
