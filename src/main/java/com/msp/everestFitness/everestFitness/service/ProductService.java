package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.dto.ProductWithImagesDto;
import com.msp.everestFitness.everestFitness.model.Category;
import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Subcategory;
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
