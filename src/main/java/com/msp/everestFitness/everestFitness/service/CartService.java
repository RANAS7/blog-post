package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.dto.CartItemDto;
import com.msp.everestFitness.everestFitness.model.CartItems;
import com.msp.everestFitness.everestFitness.model.Carts;

import java.util.UUID;

public interface CartService {
    CartItems addItemToCart(CartItemDto cartItemDto);

    CartItems updateCartItem(UUID cartItemId, Long quantity);

    void removeItemFromCart(UUID cartItemId);

    Carts getCartByUserId();



    void clearCart();
}
