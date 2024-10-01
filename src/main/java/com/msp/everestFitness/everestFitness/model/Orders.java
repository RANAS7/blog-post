package com.msp.everestFitness.everestFitness.model;

import com.msp.everestFitness.everestFitness.enumrated.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;


import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
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

//    @Transient
//    private List<OrderItems> orderItems = new ArrayList<>();

}
