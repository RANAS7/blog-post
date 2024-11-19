package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.config.LoginUtil;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.Wishlist;
import com.msp.everestFitness.everestFitness.repository.WishListRepo;
import com.msp.everestFitness.everestFitness.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WishlistServiceImpl implements WishlistService {
    @Autowired
    private WishListRepo wishListRepo;

    @Autowired
    private LoginUtil loginUtil;

    @Override
    public void createWishlist(Wishlist wishlist) {
        wishListRepo.save(wishlist);
    }

    @Override
    public List<Wishlist> getAllWishList() {
        return wishListRepo.findByUsers_UserId(loginUtil.getCurrentUserId());
    }

    @Override
    public Wishlist getWishlistById(UUID wishlistId) {
        return wishListRepo.findById(wishlistId)
                .orElseThrow(() -> new ResourceNotFoundException("The wishlist not found with the wish list Id: "+wishlistId));
    }

    @Override
    public void removeWishlist(UUID wishlistId) {
        wishListRepo.deleteById(wishlistId);
    }
}
