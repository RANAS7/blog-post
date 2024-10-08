package com.msp.everestFitness.everestFitness.model;

import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.everestFitness.enumrated.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_id", nullable = false)
    private ShippingInfo shippingInfo;

    @Column(columnDefinition = "Timestamp default current_timestamp", updatable = false)
    private Timestamp orderDate;

    @Column(nullable = false)
    private double total;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private DeliveryOpt deliveryOpt;


    @Transient
    private List<OrderItems> orderItems=new ArrayList<>();
    @Transient
    private String coupon;
    @Transient
    private String deliveryOption;
}
