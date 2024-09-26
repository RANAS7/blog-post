package com.msp.everestFitness.everestFitness.Controller;

import com.msp.everestFitness.everestFitness.dto.CartItemDto;
import com.msp.everestFitness.everestFitness.dto.UpdateCartItemDto;
import com.msp.everestFitness.everestFitness.service.CartService;
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
        return new ResponseEntity<>(cartService.addItemToCart(cartItemDto), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(@RequestParam UUID cartItemId, @RequestAttribute UpdateCartItemDto updateDto) {
        return new ResponseEntity<>(cartService.updateCartItem(cartItemId, updateDto), HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeItemFromCart(@RequestParam UUID cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @GetMapping("/")
    public ResponseEntity<?> getUserCart(@RequestParam UUID userId) {
        return new ResponseEntity<>(cartService.getCartByUserId(userId), HttpStatus.OK);
    }
}
