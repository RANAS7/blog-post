package com.msp.everestFitness.service.impl;

import com.msp.everestFitness.config.LoginUtil;
import com.msp.everestFitness.dto.CartWithCartItemsDto;
import com.msp.everestFitness.dto.UpdateCartItemQtyDto;
import com.msp.everestFitness.dto.CartItemDto;
import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.model.*;
import com.msp.everestFitness.repository.*;
import com.msp.everestFitness.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private ProductsImagesRepo productsImagesRepo;

    @Override
    public void addItemToCart(CartItemDto cartItemDto) {

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
        cartItemRepo.save(cartItem);
    }


    @Override
    public void updateCartItem(UpdateCartItemQtyDto updateCartItemQtyDto) {
        CartItems existed = cartItemRepo.findById(updateCartItemQtyDto.getItemId())
                .orElseThrow(() -> new IllegalStateException("Cart Item not found with the item id: " + updateCartItemQtyDto.getItemId()));

        Products products = productsRepo.findById(existed.getProduct().getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with the Id: " + existed.getProduct().getProductId()));

        // Check if the requested quantity is available
        if (updateCartItemQtyDto.getQuantity() > products.getStock()) {
            throw new IllegalArgumentException("Insufficient stock for product name: "
                    + products.getName() + ". Requested: "
                    + updateCartItemQtyDto.getQuantity() + ", Available: "
                    + products.getStock());
        }

        existed.setQuantity(updateCartItemQtyDto.getQuantity());
        cartItemRepo.save(existed);
    }

    @Override
    public void removeItemFromCart(UUID cartItemId) {
        CartItems cartItem = cartItemRepo.findById(cartItemId).orElseThrow(() -> new IllegalStateException("Item not found"));
        cartItemRepo.delete(cartItem);
    }

    @Override
    public List<CartWithCartItemsDto> getCartByUserId() {
        // Retrieve the current user's cart or throw an exception if not found
        Carts carts = cartRepo.findByUsers_UserId(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID: " + loginUtil.getCurrentUserId()));

        // Retrieve the list of cart items for the cart
        List<CartItems> cartItemsList = cartItemRepo.findByCarts_cartId(carts.getCartId());

        List<CartWithCartItemsDto> dtoList = new ArrayList<>();

        for (CartItems cartItems : cartItemsList) {
            CartWithCartItemsDto items = new CartWithCartItemsDto();

            items.setCartId(carts.getCartId());
            items.setQuantities(cartItems.getQuantity());
            items.setPrices(cartItems.getPrice());
            items.setItemId(cartItems.getCartItemId());
            items.setProductId(cartItems.getProduct().getProductId());

            Products products = productsRepo.findById(cartItems.getProduct().getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with the id: " + cartItems.getProduct().getProductId()));

            items.setNames(products.getName());

//            Fetch image url by product id
            ProductImages productImages = productsImagesRepo.findByProduct_ProductId(products.getProductId()).getFirst();

            items.setImageUrls(productImages.getImageUrl());

            dtoList.add(items);
        }

        return dtoList;
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
