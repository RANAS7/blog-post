package com.msp.everestFitness.everestFitness.hepler;

import com.msp.everestFitness.everestFitness.enumrated.UserType;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.*;
import com.msp.everestFitness.everestFitness.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class OrderHelper {
    @Autowired
    private CouponRepo couponRepo;

    @Autowired
    private ProductsRepo productsRepo;

    @Autowired
    private OrderItemsRepo orderItemsRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private DeliveryOptRepo deliveryOptRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private ShippingInfoRepo shippingInfoRepo;

    public Users getOrCreateGuestUser(ShippingInfo shippingInfo) {
        // Try to find the user by email
        return (Users) usersRepo.findByEmail(shippingInfo.getEmail())
                .orElseGet(() -> {
                    // Create a new user if not found
                    Users newUser = new Users();
                    newUser.setName(shippingInfo.getUsers().getName()); // Get name from Users object
                    newUser.setEmail(shippingInfo.getEmail()); // Use email from ShippingInfo
                    newUser.setUserType(UserType.GUEST);
                    newUser.setVerified(false); // Consider your application's logic
                    // Save and return the new user
                    return usersRepo.save(newUser);
                });
    }



    public ShippingInfo getShippingInfo(String email) {
        Users users = (Users) usersRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the email: "+email));
        return shippingInfoRepo.findByUsers_userId(users.getUserId());
    }


    public double calculateTotal(List<OrderItems> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public void validateMinimumOrderAmount(double total) {
        if (total < 50) {
            throw new IllegalArgumentException("Minimum order amount is $50");
        }
    }

    public double applyCoupon(String couponCode, double total) {
        if (couponCode == null || couponCode.isEmpty()) {
            return 0;
        }

        Coupons coupon = couponRepo.findByCode(couponCode);
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

        double discountAmount = switch (coupon.getDiscountType()) {
            case FIXED -> coupon.getDiscountAmount();
            case PERCENTAGE -> total * (coupon.getDiscountAmount() / 100);
        };

        return Math.min(discountAmount, coupon.getMaxDiscountAmount());
    }

    public void saveOrderItems(List<OrderItems> orderItems, Orders savedOrder) {
        for (OrderItems item : orderItems) {
            Products product = productsRepo.findById(item.getProducts().getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            item.setOrder(savedOrder);
            item.setProducts(product);
            item.setTotalAmt(item.getPrice() * item.getQuantity());
            orderItemsRepo.save(item);

            product.setStock(product.getStock() - item.getQuantity());
            productsRepo.save(product);
        }
    }

    public String createStripePaymentIntent(double amount, String email) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", (int) (amount * 100)); // Stripe expects amount in cents
        params.put("currency", "usd");
        params.put("payment_method_types", List.of("card"));
        params.put("receipt_email", email);

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getId();
    }

    public void savePaymentInfo(Orders order, String paymentIntentId, double amount) {
        Payments payment = new Payments();
        payment.setOrder(order);
        payment.setTransactionId(paymentIntentId);
        payment.setAmount(amount);
        payment.setPaymentMethod("STRIPE");
        // Save the payment (assuming you have a PaymentRepository)
        // paymentRepository.save(payment);
    }

    public void clearUserCart(UUID userId) {
        Carts cart = cartRepo.findByUsers_UserId(userId);
        if (cart != null) {
            cartItemRepo.deleteByCartId(cart.getId());
            cartRepo.deleteById(cart.getId());
        }
    }

    public DeliveryOpt getDeliveryOption(String deliveryOpt) {
        return deliveryOptRepo.findByOption(deliveryOpt);
    }
}
