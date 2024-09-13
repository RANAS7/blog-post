package com.msp.everestFitness.everestFitness.restController;

import com.msp.everestFitness.everestFitness.model.Wishlist;
import com.msp.everestFitness.everestFitness.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/")
    public ResponseEntity<?> createWishlist(@RequestBody Wishlist wishlist) {
        wishlistService.createWishlist(wishlist);
        return new ResponseEntity<>("The product added into your wishlist successfully", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getWishlist(@RequestParam(name = "wishlistId", required = false) UUID wishlistId) {
        if (wishlistId != null) {
            return new ResponseEntity<>(wishlistService.getWishlistById(wishlistId), HttpStatus.OK);
        }
        return new ResponseEntity<>(wishlistService.getAllWishList(), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> removeWishlist(@RequestParam UUID wishlistId) {
        wishlistService.removeWishlist(wishlistId);
        return new ResponseEntity<>("The product removed from your wishlist", HttpStatus.OK);
    }
}
