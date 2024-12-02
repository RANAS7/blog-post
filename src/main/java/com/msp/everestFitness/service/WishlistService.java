package com.msp.everestFitness.service;

import com.msp.everestFitness.dto.ProductWithImagesDto;
import com.msp.everestFitness.model.Wishlist;

import java.util.List;
import java.util.UUID;

public interface WishlistService {
    void createWishlist(Wishlist wishlist);


    ProductWithImagesDto getWishlistById(UUID wishlistId);

    void removeWishlist(UUID wishlistId);

    List<ProductWithImagesDto> getWishlistOfUser();
}

