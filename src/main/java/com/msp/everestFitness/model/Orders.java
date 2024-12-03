package com.msp.everestFitness.model;

import com.msp.everestFitness.enumrated.OrderStatus;
import com.msp.everestFitness.enumrated.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    private ShippingInfo shippingInfo;

    @Column(columnDefinition = "Timestamp default current_timestamp", updatable = false)
    private Timestamp orderDate;

    @Column(nullable = false)
    private double total;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private DeliveryOpt deliveryOpt;



    @Transient
    private List<OrderItems> orderItems = new ArrayList<>();
    @Transient
    private String coupon;
    @Transient
    private String deliveryOption;
}
