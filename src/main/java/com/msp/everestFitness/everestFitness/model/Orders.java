package com.msp.everestFitness.everestFitness.model;

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

    @Column(name = "order_date", nullable = false, updatable = false)
    private Timestamp orderDate = Timestamp.from(Instant.now());

    @Column(nullable = false)
    private Double total;

    @Transient
    private List<OrderItems> orderItems = new ArrayList<>();

}
