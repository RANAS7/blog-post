package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.config.LoginUtil;
import com.msp.everestFitness.everestFitness.dto.ProductWithImagesDto;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.ProductImages;
import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Users;
import com.msp.everestFitness.everestFitness.model.Wishlist;
import com.msp.everestFitness.everestFitness.repository.*;
import com.msp.everestFitness.everestFitness.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {
    @Autowired
    private WishListRepo wishListRepo;

    @Autowired
    private LoginUtil loginUtil;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private ProductsRepo productsRepo;

    @Autowired
    protected ProductsImagesRepo productsImagesRepo;

    @Autowired
    private ProductRatingRepo productRatingRepo;

    @Override
    public void createWishlist(Wishlist wishlist) {
        Users users = usersRepo.findById(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(" User not found with the id: " + loginUtil.getCurrentUserId()));
        wishlist.setUsers(users);
        wishListRepo.save(wishlist);
    }


    @Override
    public ProductWithImagesDto getWishlistById(UUID wishlistId) {
        Wishlist wishlist = wishListRepo.findById(wishlistId)
                .orElseThrow(() -> new ResourceNotFoundException("The wishlist not found with the wish list Id: " + wishlistId));


        Products products = productsRepo.findById(wishlist.getProduct().getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with the Id: " + wishlist.getWishlistId()));

        List<String> imageUrls = productsImagesRepo.findByProduct_ProductId(products.getProductId())
                .stream()
                .map(ProductImages::getImageUrl)
                .collect(Collectors.toList());


        ProductWithImagesDto dto = new ProductWithImagesDto();
        dto.setWishlistId(wishlistId);
        dto.setProductId(products.getProductId());
        dto.setName(products.getName());
        dto.setDescription(products.getDescription());
        dto.setPrice(products.getPrice());
        dto.setDiscountedPrice(products.getDiscountedPrice());
        dto.setImageUrls(imageUrls);
        dto.setRating(productRatingRepo.getAverageRatingByProductId(products.getProductId()));

        return dto;
    }

    @Override
    public void removeWishlist(UUID wishlistId) {
        wishListRepo.deleteById(wishlistId);
    }

    @Override
    public List<ProductWithImagesDto> getWishlistOfUser() {
        List<Wishlist> wishlistList = wishListRepo.findByUsers_UserId(loginUtil.getCurrentUserId());

        List<ProductWithImagesDto> dtoList = new ArrayList<>();

        for (Wishlist wishlist : wishlistList) {
            Products products = productsRepo.findById(wishlist.getProduct().getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with the Id: " + wishlist.getWishlistId()));

            List<String> imageUrls = productsImagesRepo.findByProduct_ProductId(products.getProductId())
                    .stream()
                    .map(ProductImages::getImageUrl)
                    .collect(Collectors.toList());

            ProductWithImagesDto dto = new ProductWithImagesDto();
            dto.setWishlistId(wishlist.getWishlistId());
            dto.setProductId(products.getProductId());
            dto.setName(products.getName());
            dto.setDescription(products.getDescription());
            dto.setPrice(products.getPrice());
            dto.setDiscountedPrice(products.getDiscountedPrice());
            dto.setImageUrls(imageUrls);
            dto.setRating(productRatingRepo.getAverageRatingByProductId(products.getProductId()));

            dtoList.add(dto);
        }
        return dtoList;
    }
}
