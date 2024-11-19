package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.config.LoginUtil;
import com.msp.everestFitness.everestFitness.dto.PaymentResponse;
import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.enumrated.PaymentMethod;
import com.msp.everestFitness.everestFitness.enumrated.PaymentStatus;
import com.msp.everestFitness.everestFitness.enumrated.UserType;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.*;
import com.msp.everestFitness.everestFitness.repository.*;
import com.msp.everestFitness.everestFitness.service.OrderService;
import com.msp.everestFitness.everestFitness.service.PaymentService;
import com.msp.everestFitness.everestFitness.utils.MailUtils;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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
    private PaymentService paymentService;

    @Autowired
    private LoginUtil loginUtil;

    @Override
    public PaymentResponse createOrder(Orders orders)
            throws ResourceNotFoundException, IOException, MessagingException, StripeException {
        // Fetch ShippingInfo
        ShippingInfo shippingInfo = shippingInfoRepo.findById(orders.getShippingInfo().getShippingId())
                .orElseThrow(() -> new ResourceNotFoundException("ShippingInfo not found with the id: " + orders.getShippingInfo().getShippingId()));

        // Fetch User
        Users user = usersRepo.findById(shippingInfo.getUsers().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the id: " + shippingInfo.getUsers().getUserId()));

        double total = 0.0;
        double discountAmount = 0.0;

        // Calculate total for order items
        for (OrderItems item : orders.getOrderItems()) {
            total += item.getPrice() * item.getQuantity();
        }

        // Validate minimum order amount
        if (total < 50) {
            throw new IllegalArgumentException("Please ensure your total is at least $50 to proceed with the checkout!");
        }

        // Validate coupon if provided
        if (orders.getCoupon() != null && !orders.getCoupon().isEmpty()) {
            Coupons coupon = couponRepo.findByCode(orders.getCoupon());
            if (coupon == null || !coupon.getIsActive()) {
                throw new IllegalArgumentException("Invalid or inactive coupon code");
            }

            Timestamp currentTime = Timestamp.from(Instant.now());
            if (coupon.getValidFrom().after(currentTime) || (coupon.getValidUntil() != null && coupon.getValidUntil().before(currentTime))) {
                throw new IllegalArgumentException("The coupon is not valid for this period");
            }

            if (total < coupon.getMinimumOrderAmount()) {
                throw new IllegalArgumentException("The order does not meet the minimum amount required for the coupon");
            }

            // Calculate discount amount based on coupon type
            discountAmount = switch (coupon.getDiscountType()) {
                case FIXED -> coupon.getDiscountAmount();
                case PERCENTAGE -> total * (coupon.getDiscountAmount() / 100);
            };
        }

        // Fetch delivery option and calculate delivery charge
        DeliveryOpt deliveryOpt = deliveryOptRepo.findByOption(orders.getDeliveryOption());
        double deliveryCharge = deliveryOpt.getCharge();

        // Create and save new order
        Orders newOrder = new Orders();
        newOrder.setTotal(total + deliveryCharge - discountAmount);
        newOrder.setOrderDate(Timestamp.from(Instant.now()));
        newOrder.setShippingInfo(shippingInfo);
        newOrder.setPaymentMethod(orders.getPaymentMethod());
        newOrder.setCoupon(orders.getCoupon());
        newOrder.setDeliveryOpt(deliveryOpt);

        // Set order status based on payment method
        if (orders.getPaymentMethod().equals(PaymentMethod.STRIPE)) {
            newOrder.setOrderStatus(OrderStatus.PENDING);
        } else {
            newOrder.setOrderStatus(OrderStatus.COMPLETED);
        }
        Orders savedOrder = ordersRepo.save(newOrder);

        // Save order items
        for (OrderItems item : orders.getOrderItems()) {
            OrderItems newItem = new OrderItems(); // Create new instance for each item
            newItem.setOrder(savedOrder);
            newItem.setProducts(item.getProducts());
            newItem.setQuantity(item.getQuantity());
            newItem.setPrice(item.getPrice());
            newItem.setTotalAmt(item.getQuantity() * item.getPrice());
            orderItemsRepo.save(newItem); // Save the new item
        }

        Payments payments = new Payments();
        payments.setPaymentStatus(PaymentStatus.PENDING);
        payments.setOrders(savedOrder);
        payments.setAmount(savedOrder.getTotal());
        paymentsRepo.save(payments);        // Handle payment if Stripe is used
        if (orders.getPaymentMethod().equals(PaymentMethod.STRIPE)) {
            return paymentService.createPaymentLink(savedOrder);
        } else if (orders.getPaymentMethod().equals(PaymentMethod.CASH_ON_DELIVERY)) {
            mailUtils.sendOrderConfirmationMail(savedOrder.getShippingInfo().getUsers().getEmail(), savedOrder);

        }

        return new PaymentResponse(); // Return response for completed order
    }


    //    Create order for GUEST
    @Override
    public PaymentResponse createGuestOrder(Orders orders, ShippingInfo guestShippingInfo)
            throws ResourceNotFoundException, IOException, MessagingException, StripeException {

        Users users = (Users) usersRepo.findByEmail(guestShippingInfo.getEmail())
                .orElseGet(() -> {
                    Users newUser = new Users();
                    newUser.setEmail(guestShippingInfo.getEmail());
                    newUser.setName(guestShippingInfo.getName());
                    newUser.setUserType(UserType.GUEST);
                    return usersRepo.save(newUser);
                });

        // Save guest shipping info
        guestShippingInfo.setUsers(users);
        ShippingInfo savedShippingInfo = shippingInfoRepo.save(guestShippingInfo);

        double total = 0.0;
        double discountAmount = 0.0;

        // Calculate total for order items
        for (OrderItems item : orders.getOrderItems()) {
            total += item.getPrice() * item.getQuantity();
        }

        // Validate minimum order amount
        if (total < 50) {
            throw new IllegalArgumentException("Please ensure your total is at least $50 to proceed with the checkout!");
        }

        // Validate coupon if provided
        if (orders.getCoupon() != null && !orders.getCoupon().isEmpty()) {
            Coupons coupon = couponRepo.findByCode(orders.getCoupon());
            if (coupon == null || !coupon.getIsActive()) {
                throw new IllegalArgumentException("Invalid or inactive coupon code");
            }

            Timestamp currentTime = Timestamp.from(Instant.now());
            if (coupon.getValidFrom().after(currentTime) || (coupon.getValidUntil() != null && coupon.getValidUntil().before(currentTime))) {
                throw new IllegalArgumentException("The coupon is not valid for this period");
            }

            if (total < coupon.getMinimumOrderAmount()) {
                throw new IllegalArgumentException("The order does not meet the minimum amount required for the coupon");
            }

            // Calculate discount amount based on coupon type
            discountAmount = switch (coupon.getDiscountType()) {
                case FIXED -> coupon.getDiscountAmount();
                case PERCENTAGE -> total * (coupon.getDiscountAmount() / 100);
            };
        }

        // Fetch delivery option and calculate delivery charge
        DeliveryOpt deliveryOpt = deliveryOptRepo.findByOption(orders.getDeliveryOption());
        double deliveryCharge = deliveryOpt.getCharge();

        // Create and save new order
        Orders newOrder = new Orders();
        newOrder.setTotal(total + deliveryCharge - discountAmount);
        newOrder.setOrderDate(Timestamp.from(Instant.now()));
        newOrder.setShippingInfo(savedShippingInfo);
        newOrder.setPaymentMethod(orders.getPaymentMethod());
        newOrder.setCoupon(orders.getCoupon());
        newOrder.setDeliveryOpt(deliveryOpt);

        // Set order status based on payment method
        if (orders.getPaymentMethod().equals(PaymentMethod.STRIPE)) {
            newOrder.setOrderStatus(OrderStatus.PENDING);
        } else {
            newOrder.setOrderStatus(OrderStatus.COMPLETED);
        }
        Orders savedOrder = ordersRepo.save(newOrder);

        // Save order items
        for (OrderItems item : orders.getOrderItems()) {
            OrderItems newItem = new OrderItems();
            newItem.setOrder(savedOrder);
            newItem.setProducts(item.getProducts());
            newItem.setQuantity(item.getQuantity());
            newItem.setPrice(item.getPrice());
            newItem.setTotalAmt(item.getQuantity() * item.getPrice());
            orderItemsRepo.save(newItem);
        }

        Payments payments = new Payments();
        payments.setPaymentStatus(PaymentStatus.PENDING);
        payments.setOrders(savedOrder);
        payments.setAmount(savedOrder.getTotal());
        paymentsRepo.save(payments);

        // Handle payment and send confirmation email if applicable
        if (orders.getPaymentMethod().equals(PaymentMethod.STRIPE)) {
            return paymentService.createPaymentLink(savedOrder);
        } else if (orders.getPaymentMethod().equals(PaymentMethod.CASH_ON_DELIVERY)) {
            mailUtils.sendOrderConfirmationMail(savedShippingInfo.getEmail(), savedOrder);
        }

        return new PaymentResponse(); // Return appropriate response for completed order
    }

    @Override
    public List<Orders> getOrderOfUser() {
        return ordersRepo.findByShippingInfo_Users_UserId(loginUtil.getCurrentUserId());
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
