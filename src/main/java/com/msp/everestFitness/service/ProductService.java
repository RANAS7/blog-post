package com.msp.everestFitness.service;

import com.msp.everestFitness.dto.ProductWithImagesDto;
import com.msp.everestFitness.model.Products;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    void addProduct(Products products, UUID subcategoryId,List<MultipartFile> images) throws IOException;
    List<ProductWithImagesDto> getAllProductsWithImages();
    ProductWithImagesDto getProductByIdWithImages(UUID productId);
    void deleteProductById(UUID productId) throws IOException;

    // Suggest products based on subcategory
    List<ProductWithImagesDto> getSuggestedProductsBySubcategory(UUID subcategoryId);

    // Suggest products based on discounted price
    List<ProductWithImagesDto> getSuggestedProductsByDiscount(double minDiscount);


    List<ProductWithImagesDto> getSuggestedProductsByCategory(UUID categoryId);

    List<ProductWithImagesDto> getSuggestedProducts(UUID subcategoryId, double minDiscount);

    List<ProductWithImagesDto> getPopularProducts();
}
