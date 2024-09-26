package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.enumrated.UserType;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.*;
import com.msp.everestFitness.everestFitness.repository.*;
import com.msp.everestFitness.everestFitness.service.OrderService;
import com.msp.everestFitness.everestFitness.utils.MailUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
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


    //    Create order for USER and MEMBER
    @Override
    public void createOrder(Orders order, String couponCode) throws MessagingException, IOException {

        // Fetch the ShippingInfo
        ShippingInfo shippingInfo = shippingInfoRepo.findById(order.getShippingInfo().getShippingId())
                .orElseThrow(() -> new ResourceNotFoundException("The shipping info not found"));

        // Fetch the Users from ShippingInfo
        Users users = usersRepo.findById(shippingInfo.getUsers().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("The user not found"));

        // Validate minimum order amount
        if (order.getTotal() < 50) {
            throw new IllegalArgumentException("Minimum order amount is $50");
        }

        // Set the correct shipping info in the order
        order.setShippingInfo(shippingInfo);

        // Apply coupon if couponCode is provided
        double discountAmount = 0.0;

        if (couponCode != null && !couponCode.isEmpty()) {
            Coupons coupon = couponRepo.findByCode(couponCode);
            if (coupon == null) {
                throw new IllegalArgumentException("Invalid or expired coupon code");
            }

            // Validate if the coupon is active
            if (!coupon.getIsActive()) {
                throw new IllegalArgumentException("The coupon is inactive");
            }

            // Validate coupon's validity period
            Timestamp currentTime = Timestamp.from(Instant.now());
            if (coupon.getValidFrom().after(currentTime) || (coupon.getValidUntil() != null && coupon.getValidUntil().before(currentTime))) {
                throw new IllegalArgumentException("The coupon is not valid for this period");
            }

            // Check if the order meets the coupon's minimum order amount
            if (order.getTotal() < coupon.getMinimumOrderAmount()) {
                throw new IllegalArgumentException("The order does not meet the minimum amount required for the coupon");
            }

            // Apply discount based on discount type (FIXED or PERCENTAGE)
            discountAmount = switch (coupon.getDiscountType()) {
                case FIXED -> coupon.getDiscountAmount();
                case PERCENTAGE -> order.getTotal() * (coupon.getDiscountAmount() / 100);
                default -> throw new IllegalArgumentException("Invalid discount type");
            };

            // Ensure discount does not exceed the maximum allowed amount
            if (discountAmount > coupon.getMaxDiscountAmount()) {
                discountAmount = coupon.getMaxDiscountAmount();
            }

            // Reduce the total by the discount amount
            order.setTotal(order.getTotal() - discountAmount);
        }

        // Save the order
        Orders savedOrder = ordersRepo.save(order);

        // Calculate the grand total of all order items
        double grandTotal = 0.0;

        // Save the order items
        for (OrderItems item : order.getOrderItems()) {
            Products product = productsRepo.findById(item.getProducts().getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            double totalAmt = item.getPrice() * item.getQuantity();
            item.setProducts(product);
            item.setOrder(savedOrder);
            item.setTotalAmt(totalAmt);

            // Save each order item
            orderItemsRepo.save(item);

            // Update the product's stock
            product.setStock(product.getStock() - item.getQuantity());
            productsRepo.save(product);

            grandTotal += totalAmt;
        }

        // Apply coupon discount to the grand total
        grandTotal = grandTotal - discountAmount;

        // Update order total
        savedOrder.setTotal(grandTotal);
        ordersRepo.save(savedOrder);

        // Send confirmation mail to the user's email
        mailUtils.sendOrderConfirmationMail(users.getEmail(), savedOrder.getOrderId());

        Carts cart= cartRepo.findByUsers_UserId(users.getUserId());

        cartItemRepo.deleteByCartId(cart.getId());
        cartRepo.deleteById(cart.getId());
    }


    //    Create order for GUEST
    @Override
    public void createGuestOrder(Orders order, String couponCode) throws MessagingException, IOException {

        // Validate minimum order amount
        if (order.getTotal() < 50) {
            throw new IllegalArgumentException("Minimum order amount is $50");
        }

        // Check if the user exists, if not, create a new guest user
        Users user = order.getShippingInfo().getUsers();
        if (user == null || user.getUserId() == null) {
            String name = order.getShippingInfo().getUsers().getName();
            String email = order.getShippingInfo().getUsers().getEmail();

            user = (Users) usersRepo.findByEmail(email).orElseGet(() -> {
                Users newUser = new Users();
                newUser.setName(name);
                newUser.setEmail(email);
                newUser.setUserType(UserType.GUEST);
                newUser.setVerified(true);
                return usersRepo.save(newUser);
            });

            order.getShippingInfo().setUsers(user);
        }

        // Fetch the ShippingInfo
        ShippingInfo shippingInfo = shippingInfoRepo.findById(order.getShippingInfo().getShippingId())
                .orElseThrow(() -> new ResourceNotFoundException("The shipping info not found"));

        order.setShippingInfo(shippingInfo);

        // Apply coupon discount logic (if applicable)
        double discountAmount = 0.0;
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupons coupon = couponRepo.findByCode(couponCode);
            if (coupon == null) {
                throw new IllegalArgumentException("Invalid or expired coupon code");
            }

            if (!coupon.getIsActive()) {
                throw new IllegalArgumentException("Coupon is inactive");
            }

            Timestamp currentTime = Timestamp.from(Instant.now());
            if (coupon.getValidUntil() != null && coupon.getValidUntil().before(currentTime)) {
                throw new IllegalArgumentException("Coupon has expired");
            }

            // Apply discount based on the type
            discountAmount = switch (coupon.getDiscountType()) {
                case PERCENTAGE -> order.getTotal() * (coupon.getDiscountAmount() / 100);
                case FIXED -> coupon.getDiscountAmount();
                default -> throw new IllegalArgumentException("Invalid discount type");
            };

            if (discountAmount > coupon.getMaxDiscountAmount()) {
                discountAmount = coupon.getMaxDiscountAmount();
            }

            order.setTotal(order.getTotal() - discountAmount);
        }

        // Save the order
        Orders savedOrder = ordersRepo.save(order);

        double grandTotal = 0.0;
        for (OrderItems item : order.getOrderItems()) {
            Products product = productsRepo.findById(item.getProducts().getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            double totalAmt = item.getPrice() * item.getQuantity();
            item.setProducts(product);
            item.setOrder(savedOrder);
            item.setTotalAmt(totalAmt);

            orderItemsRepo.save(item);

            product.setStock(product.getStock() - item.getQuantity());
            productsRepo.save(product);

            grandTotal += totalAmt;
        }

        savedOrder.setTotal(grandTotal - discountAmount);
        ordersRepo.save(savedOrder);

        mailUtils.sendOrderConfirmationMail(user.getEmail(), savedOrder.getOrderId());
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
