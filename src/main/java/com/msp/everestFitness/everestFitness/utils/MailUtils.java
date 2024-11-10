package com.msp.everestFitness.everestFitness.utils;

import com.msp.everestFitness.everestFitness.model.DeliveryOpt;
import com.msp.everestFitness.everestFitness.model.OrderItems;
import com.msp.everestFitness.everestFitness.model.Orders;
import com.msp.everestFitness.everestFitness.repository.DeliveryOptRepo;
import com.msp.everestFitness.everestFitness.repository.OrderItemsRepo;
import com.msp.everestFitness.everestFitness.repository.OrdersRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.Year;
import java.util.List;


@Service
public class MailUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${DOMAIN}")
    private String domain;

    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private OrderItemsRepo orderItemsRepo;

    @Autowired
    private DeliveryOptRepo deliveryOptRepo;

    String currentYear = String.valueOf(Year.now().getValue());

    private static final Logger log = LoggerFactory.getLogger(MailUtils.class);

    //Mail configuration for Reset password
    public void sendPasswordResetEmail(String toEmail, String token, String recipientName) throws MessagingException {
        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String subject = "Reset Your Password with This Simple Click!";

            String resetLink = domain + "/api/auth/reset-form?token=" + token;


            ClassPathResource htmlFile = new ClassPathResource("templates/PasswordReset.html");
            String htmlContent = StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[RESET_LINK]", String.valueOf(resetLink))
                    .replace("[User]", recipientName)
                    .replace("[Year]", currentYear);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error:" + e.getMessage(), e);
        }

    }

    //Mail configuration for user verification
    public void sendEmailVerificationMail(String toEmail, String token, String recipientName) throws MessagingException, IOException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String subject = "Email Verification";

            String verificationLink = domain + "/api/auth/verify-email?token=" + token;

            ClassPathResource htmlFile = new ClassPathResource("templates/EmailVerification.html");
            String htmlContent = StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("[VERIFICATION_LINK]", verificationLink);
            htmlContent = htmlContent.replace("[User]", recipientName);
            htmlContent = htmlContent.replace("[Year]", currentYear);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error:" + e.getMessage(), e);
        }
    }


    public void sendOrderConfirmationMail(String toEmail, Orders savedOrder) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");


        String subject = "Order Confirmation - " + savedOrder.getOrderId();

        String estimatedDeliveryDate = String.valueOf(Timestamp.from(Instant.now().plusSeconds(3 * 24 * 60 * 60)));

        // Load the HTML template
        ClassPathResource htmlFile = new ClassPathResource("templates/OrderConfirmation.html");
        String htmlContent = StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);


        List<OrderItems> orderItems = orderItemsRepo.findByOrder_OrderId(savedOrder.getOrderId());

        // Loop through the order items and populate the table rows
        StringBuilder orderItemsHtml = new StringBuilder();
        int sn = 0;

        for (OrderItems item : orderItems) {

            String productName = item.getProducts().getName(); // Assuming getProduct() has a getName() method
            int quantity = item.getQuantity();
            double price = item.getPrice();
            double total = price * quantity;


                    orderItemsHtml.append("<tr>")
                            .append("<td>").append(sn += 1).append("</td>")
                            .append("<td>").append(productName).append("</td>")
                            .append("<td>").append(quantity).append("</td>")
                            .append("<td>$").append(price).append("</td>")
                            .append("<td>$").append(total).append("</td>")
                            .append("</tr>");
        }
        DeliveryOpt deliveryOpt =deliveryOptRepo.findById(savedOrder.getDeliveryOpt().getOptionId()).get();


        // Inject the dynamic order items and other placeholders into the template
        htmlContent = htmlContent.replace("${orderItemsHtml}", orderItemsHtml.toString())
                .replace("${customerName}", savedOrder.getShippingInfo().getUsers().getName())  // Correct method for customer name
                .replace("${orderId}", savedOrder.getOrderId().toString())
                .replace("${orderDate}", savedOrder.getOrderDate().toString())
                .replace("${deliveryDate}", estimatedDeliveryDate) // Adjust delivery date if needed
                .replace("${charge}", String.valueOf(deliveryOpt.getCharge()))
                .replace("${orderTotal}", String.valueOf(savedOrder.getTotal()))
                .replace("${shippingAddress}", savedOrder.getShippingInfo().getAddress())
                .replace("${shippingCity}", savedOrder.getShippingInfo().getCity())
                .replace("${shippingState}", savedOrder.getShippingInfo().getState())
                .replace("${shippingPostalCode}", savedOrder.getShippingInfo().getPostalCode())
                .replace("${shippingCountry}", savedOrder.getShippingInfo().getCountry())
                .replace("${shippingPhoneNumber}", savedOrder.getShippingInfo().getPhoneNumber())
                .replace("[Year]", currentYear);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        // Set the HTML content
        helper.setText(htmlContent, true);

        // Send the email
        javaMailSender.send(mimeMessage);
    }

}
