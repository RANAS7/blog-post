package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.dto.CartItemDto;
import com.msp.everestFitness.everestFitness.dto.UpdateCartItemDto;
import com.msp.everestFitness.everestFitness.model.CartItems;
import com.msp.everestFitness.everestFitness.model.Carts;
import com.msp.everestFitness.everestFitness.repository.CartItemRepo;
import com.msp.everestFitness.everestFitness.repository.CartRepo;
import com.msp.everestFitness.everestFitness.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;


    @Override
    public CartItems addItemToCart(CartItemDto cartItemDto) {
        Carts cart = cartRepo.findByUsers_UserId(cartItemDto.getUsers().getUserId());
        List<CartItems> existingItem = cartItemRepo.findByCartAndProduct(cart.getCartId(), cartItemDto.getProducts().getProductId());

        CartItems cartItem;
        if (existingItem != null) {
            cartItem = existingItem.getFirst();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDto.getQuantity());
        } else {
            cartItem = new CartItems();
            cartItem.setCarts(cart);
            cartItem.setProduct(cartItemDto.getProducts());
            cartItem.setQuantity(cartItemDto.getQuantity());
            cartItem.setPrice(cartItemDto.getPrice());
        }
        return cartItemRepo.save(cartItem);
    }

    @Override
    public CartItems updateCartItem(UUID cartItemId, UpdateCartItemDto updateDto) {
        CartItems cartItem = cartItemRepo.findById(cartItemId).orElseThrow(() -> new IllegalStateException("Item not found"));
        cartItem.setQuantity(updateDto.getQuantity());
        return cartItemRepo.save(cartItem);
    }

    @Override
    public void removeItemFromCart(UUID cartItemId) {
        CartItems cartItem = cartItemRepo.findById(cartItemId).orElseThrow(() -> new IllegalStateException("Item not found"));
        cartItemRepo.delete(cartItem);
    }

    @Override
    public Carts getCartByUserId(UUID userId) {
        return cartRepo.findByUserIdAndIsActive(userId, true)
                .orElseGet(() -> createNewCart(userId));
    }

    @Override
    public Carts getOrCreateCart(UUID userId) {
        return cartRepo.findByUserIdAndIsActive(userId, true).orElseGet(() -> createNewCart(userId));
    }

    @Override
    public Carts createNewCart(UUID userId) {

        Carts cart = cartRepo.findByUsers_UserId(userId);
        cart.setUsers(cart.getUsers());
        return cartRepo.save(cart);
    }

    @Override
    public void clearCart(UUID userId) {
        Carts carts = cartRepo.findByUsers_UserId(userId);

        List<CartItems> cartItemsList = cartItemRepo.findByCarts_cartId(carts.getCartId());
        for (CartItems items : cartItemsList) {
            cartItemRepo.deleteById(items.getCartItemId());
        }
        cartRepo.deleteById(carts.getCartId());
    }
}
