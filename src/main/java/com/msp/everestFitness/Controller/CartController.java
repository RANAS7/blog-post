package com.msp.everestFitness.Controller;

import com.msp.everestFitness.dto.UpdateCartItemQtyDto;
import com.msp.everestFitness.dto.CartItemDto;
import com.msp.everestFitness.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addItemToCart(@RequestBody CartItemDto cartItemDto) {
        cartService.addItemToCart(cartItemDto);
        return new ResponseEntity<>("item added to cart successfully", HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCartItem(@RequestBody UpdateCartItemQtyDto updateCartItem) {
        cartService.updateCartItem(updateCartItem);
        return new ResponseEntity<>("Item quantity updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeItemFromCart(@RequestParam UUID cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @GetMapping("/")
    public ResponseEntity<?> getUserCart() {
        return new ResponseEntity<>(cartService.getCartByUserId(), HttpStatus.OK);
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearCart() {
        cartService.clearCart();
        return new ResponseEntity<>("Cart have been cleared", HttpStatus.OK);
    }
}