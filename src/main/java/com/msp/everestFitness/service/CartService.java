package com.msp.everestFitness.service;

import com.msp.everestFitness.dto.CartItemDto;
import com.msp.everestFitness.dto.CartWithCartItemsDto;
import com.msp.everestFitness.dto.UpdateCartItemQtyDto;

import java.util.List;
import java.util.UUID;

public interface CartService {
    void addItemToCart(CartItemDto cartItemDto);

    void updateCartItem(UpdateCartItemQtyDto updateCartItemQtyDto);

    void removeItemFromCart(UUID cartItemId);

    List<CartWithCartItemsDto> getCartByUserId();



    void clearCart();
}
