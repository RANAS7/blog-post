package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.config.LoginUtil;
import com.msp.everestFitness.everestFitness.dto.CartItemDto;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.CartItems;
import com.msp.everestFitness.everestFitness.model.Carts;
import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Users;
import com.msp.everestFitness.everestFitness.repository.CartItemRepo;
import com.msp.everestFitness.everestFitness.repository.CartRepo;
import com.msp.everestFitness.everestFitness.repository.ProductsRepo;
import com.msp.everestFitness.everestFitness.repository.UsersRepo;
import com.msp.everestFitness.everestFitness.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private LoginUtil loginUtil;

    @Autowired
    private ProductsRepo productsRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Override
    public CartItems addItemToCart(CartItemDto cartItemDto) {

        // Fetch the user and throw an exception if not found
        Users users = usersRepo.findById(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the id: " + loginUtil.getCurrentUserId()));

        // Fetch the cart for the user or create a new one if it doesn't exist
        Carts cart = cartRepo.findByUsers_UserId(loginUtil.getCurrentUserId())
                .orElseGet(() -> {
                    Carts newCart = new Carts();
                    newCart.setUsers(users);
                    return cartRepo.save(newCart);
                });

        // Log the created or fetched cart ID
        System.out.println("Cart ID: " + cart.getCartId());

        // Check if the product already exists in the cart
        List<CartItems> existingItems = cartItemRepo.findByCartAndProduct(cart.getCartId(), cartItemDto.getProducts().getProductId());
        CartItems cartItem;

        if (!existingItems.isEmpty()) {
            // Update quantity if item already exists
            cartItem = existingItems.get(0);
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDto.getQuantity());
        } else {
            // Create a new cart item
            cartItem = new CartItems();
            cartItem.setCarts(cart);
            cartItem.setProduct(cartItemDto.getProducts());
            cartItem.setQuantity(cartItemDto.getQuantity());
            cartItem.setPrice(cartItemDto.getPrice());
        }

        // Save and return the updated/created cart item
        return cartItemRepo.save(cartItem);
    }


    @Override
    public CartItems updateCartItem(UUID cartItemId, Long quantity) {
        CartItems cartItem = cartItemRepo.findById(cartItemId).orElseThrow(() -> new IllegalStateException("Item not found"));

        Products products = productsRepo.findById(cartItem.getProduct().getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with the Id: " + cartItem.getProduct().getProductId()));

        // Check if the requested quantity is available
        if (quantity > products.getStock()) {
            throw new IllegalArgumentException("Insufficient stock for product name: "
                    + products.getName() + ". Requested: "
                    + quantity + ", Available: "
                    + products.getStock());
        }

        cartItem.setQuantity(quantity);
        return cartItemRepo.save(cartItem);
    }

    @Override
    public void removeItemFromCart(UUID cartItemId) {
        CartItems cartItem = cartItemRepo.findById(cartItemId).orElseThrow(() -> new IllegalStateException("Item not found"));
        cartItemRepo.delete(cartItem);
    }

    @Override
    public Carts getCartByUserId() {
        return cartRepo.findByUsers_UserId(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found associated with the user id: " + loginUtil.getCurrentUserId()));
    }


    @Override
    public void clearCart() {
        Carts carts = cartRepo.findByUsers_UserId(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found associated with the user id: " + loginUtil.getCurrentUserId()));

        List<CartItems> cartItemsList = cartItemRepo.findByCarts_cartId(carts.getCartId());
        for (CartItems items : cartItemsList) {
            cartItemRepo.deleteById(items.getCartItemId());
        }
        cartRepo.deleteById(carts.getCartId());
    }
}
