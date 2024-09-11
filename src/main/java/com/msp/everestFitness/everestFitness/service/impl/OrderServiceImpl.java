package com.msp.everestFitness.everestFitness.service.impl;

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


    //    Create order for USER and MEMBER
    @Override
    public void createOrder(Orders order) throws MessagingException, IOException {

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

        // Save the order
        Orders savedOrder = ordersRepo.save(order);

        // Calculate the grand total of all order items
        double grandTotal = 0.0;

        // Save the order items
        for (OrderItems item : order.getOrderItems()) {
            // Ensure the product is not null
            if (item.getProducts() == null || item.getProducts().getProductId() == null) {
                throw new IllegalArgumentException("Product information is missing for one or more items");
            }

            // Fetch the product from the database using the product ID to avoid transient issues
            Products product = productsRepo.findById(item.getProducts().getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            double totalAmt = item.getPrice() * item.getQuantity();

            // Set the fetched product and saved order for the item
            item.setProducts(product);
            item.setOrder(savedOrder);
            item.setTotalAmt(totalAmt);

            // Save each order item
            orderItemsRepo.save(item);

            Products products=productsRepo.findById(item.getProducts().getProductId())
                    .orElseThrow(()-> new ResourceNotFoundException("The Product nto found with the id: "+item.getProducts().getProductId()));


            products.setStock(products.getStock()-item.getQuantity());

            productsRepo.save(products);

            // Add to the grand total
            grandTotal += totalAmt;
        }

        Orders orders=ordersRepo.findById(savedOrder.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("The order does not exist in our record"));

        orders.setTotal(grandTotal);
        ordersRepo.save(orders);


        // Send confirmation mail to the user's email
        mailUtils.sendOrderConfirmationMail(users.getEmail(), savedOrder.getOrderId());
    }


    //    Create order for GUEST
    @Override
    public void createGuestOrder(Orders order) throws MessagingException, IOException {


        // Validate minimum order amount
        if (order.getTotal() < 50) {
            throw new IllegalArgumentException("Minimum order amount is $50");
        }

        // Check if the user exists, if not, create a new guest user
        Users user = order.getShippingInfo().getUsers();
        if (user == null || user.getUserId() == null) {
            // Extract user details from shippingInfo
            assert order.getShippingInfo().getUsers() != null;
            String name = order.getShippingInfo().getUsers().getName();
            String email = order.getShippingInfo().getUsers().getEmail();

            // Create or update the user
            user = (Users) usersRepo.findByEmail(email).orElseGet(() -> {
                Users newUser = new Users();
                newUser.setName(name);
                newUser.setEmail(email);
                newUser.setUserType(UserType.GUEST); // Mark as guest
                newUser.setVerified(true);
                return usersRepo.save(newUser);
            });

            // Set the user to the order's shipping info
            order.getShippingInfo().setUsers(user);
            order.setShippingInfo(order.getShippingInfo());
        }

        // Save order
        Orders savedOrder = ordersRepo.save(order);

        // Save order items
        for (OrderItems item : order.getOrderItems()) {
            item.setOrder(savedOrder);
            orderItemsRepo.save(item);
        }

        // Send confirmation email
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

//    @Override
//    public Orde getOrderDetails(UUID orderId) {
//        Orders order = getOrderById(orderId);
//        List<OrderItems> orderItems = ordersRepo.getOrderItemsByOrderId(orderId);
//        ShippingInfo shippingInfo = (ShippingInfo) shippingInfoRepo.findByUsers_UserId(order.getShippingInfo().getUsers().getUserId());
//
//        if (orderItems.isEmpty()) {
//            throw new ResourceNotFoundException("Order items not found for order id " + orderId);
//        }
//        if (shippingInfo == null) {
//            throw new ResourceNotFoundException("Shipping info not found for user id " + order.getShippingInfo().getUsers().getUserId());
//        }
//        return null;
//    }
}
