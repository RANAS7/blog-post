package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.dto.CartItemDto;
import com.msp.everestFitness.everestFitness.dto.CartWithCartItemsDto;
import com.msp.everestFitness.everestFitness.model.CartItems;

import java.util.List;
import java.util.UUID;

public interface CartService {
    CartItems addItemToCart(CartItemDto cartItemDto);

    CartItems updateCartItem(UUID cartItemId, Long quantity);

    void removeItemFromCart(UUID cartItemId);

    List<CartWithCartItemsDto> getCartByUserId();



    void clearCart();
}
