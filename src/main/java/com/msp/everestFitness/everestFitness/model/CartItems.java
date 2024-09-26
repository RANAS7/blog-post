package com.msp.everestFitness.everestFitness.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Table(name = "cart_items")
public class CartItems {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Carts cart;

    @ManyToOne(fetch = FetchType.LAZY)
    private Products product;
    private int quantity;
    private BigDecimal price;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
