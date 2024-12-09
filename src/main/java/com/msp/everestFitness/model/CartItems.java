package com.msp.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Table
public class CartItems {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID cartItemId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Carts carts;

    @ManyToOne(fetch = FetchType.EAGER)
    private Products product;
    private Long quantity;
    private BigDecimal price;

    @Column(updatable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp createdAt;
    @Column(insertable = false, columnDefinition = "Timestamp default current_timestamp")
    private Timestamp updatedAt;
}