package com.msp.everestFitness.everestFitness.model;

import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.enumrated.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;


import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;

    @ManyToOne
    @JoinColumn(name = "shipping_id", nullable = false)
    private ShippingInfo shippingInfo;

    @Column(columnDefinition = "Timestamp default current_timestamp", updatable = false)
    private Timestamp orderDate;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToOne
    @JoinColumn(nullable = false)
    private DeliveryOpt deliveryOpt;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Transient
    private List<OrderItems> orderItems;

    @Transient
    private String coupon;
    @Transient
    private String deliveryOption;

    @PrePersist
    protected void onCreate() {
        if (this.orderId == null) {
            this.orderId = UUID.randomUUID();
        }
    }
}
