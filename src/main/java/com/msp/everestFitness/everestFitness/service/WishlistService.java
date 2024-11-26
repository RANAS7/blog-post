package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.dto.ProductWithImagesDto;
import com.msp.everestFitness.everestFitness.model.Wishlist;

import java.util.List;
import java.util.UUID;

public interface WishlistService {
    void createWishlist(Wishlist wishlist);


    ProductWithImagesDto getWishlistById(UUID wishlistId);

    void removeWishlist(UUID wishlistId);

    List<ProductWithImagesDto> getWishlistOfUser();
}

