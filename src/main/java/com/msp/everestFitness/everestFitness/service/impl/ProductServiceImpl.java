package com.msp.everestFitness.everestFitness.service.impl;

import com.cloudinary.Cloudinary;
import com.msp.everestFitness.everestFitness.dto.ProductWithImagesDto;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.ProductImages;
import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Subcategory;
import com.msp.everestFitness.everestFitness.repository.ProductsImagesRepo;
import com.msp.everestFitness.everestFitness.repository.ProductsRepo;
import com.msp.everestFitness.everestFitness.repository.SubcategoryRepo;
import com.msp.everestFitness.everestFitness.service.ProductService;
import com.msp.everestFitness.everestFitness.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductsRepo productsRepo;

    @Autowired
    private ProductsImagesRepo productsImagesRepo;

    @Autowired
    private SubcategoryRepo subcategoryRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Value("${CLOUDINARY_FOLDER_NAME}")
    private String cloudinaryFolderName;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public void addProduct(Products products, UUID subcategoryId, List<MultipartFile> images) throws IOException {
        Subcategory subcategory = subcategoryRepo.findById(subcategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with Id: " + subcategoryId));

        Products existingProduct = null;

        // If the product ID is present, update the existing product
        if (products.getProductId() != null) {
            existingProduct = productsRepo.findById(products.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with Id: " + products.getProductId()));

            existingProduct.setName(products.getName());
            existingProduct.setDescription(products.getDescription());
            existingProduct.setPrice(products.getPrice());
            existingProduct.setSubcategory(subcategory);
            existingProduct.setDiscountedPrice(products.getDiscountedPrice());

            // Save the updated product
            existingProduct = productsRepo.save(existingProduct);
        } else {
            // If it's a new product, set the subcategory and save the product
            products.setSubcategory(subcategory);
            existingProduct = productsRepo.save(products);
        }

        // Check if the total number of images exceeds the limit of 4
        if (images.size() + images.size() > 4) {
            throw new IllegalArgumentException("You can only upload maximum 4 images for a product.");
        }

        // Save images for the product
        for (MultipartFile file : images) {
            ProductImages productImage = new ProductImages();

            // Upload the file to Cloudinary
            String imageUrl = fileUtils.uploadFileToCloudinary(file);


            // Set image details
            productImage.setProduct(existingProduct);
            productImage.setImageUrl(imageUrl); // Directly store the URL
            productsImagesRepo.save(productImage);
        }
    }


    @Override
    public List<ProductWithImagesDto> getAllProductsWithImages() {
        List<Products> productsList = productsRepo.findAll();

        return productsList.stream().map(product -> {
            List<String> imageUrls = productsImagesRepo.findByProduct_ProductId(product.getProductId())
                    .stream()
                    .map(ProductImages::getImageUrl)
                    .collect(Collectors.toList());

            ProductWithImagesDto dto = new ProductWithImagesDto();
            dto.setProductId(product.getProductId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setDiscountedPrice(product.getDiscountedPrice());
            dto.setImageUrls(imageUrls);

            return dto;
        }).collect(Collectors.toList());
    }



    @Override
    public ProductWithImagesDto getProductByIdWithImages(UUID productId) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " was not found."));

        List<String> imageUrls = productsImagesRepo.findByProduct_ProductId(product.getProductId())
                .stream()
                .map(ProductImages::getImageUrl)
                .collect(Collectors.toList());

        ProductWithImagesDto dto = new ProductWithImagesDto();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountedPrice(product.getDiscountedPrice());
        dto.setImageUrls(imageUrls);

        return dto;
    }

    @Override
    public void deleteProductById(UUID productId) throws IOException {
        // Fetch all images associated with the product
        List<ProductImages> productImagesList = productsImagesRepo.findByProduct_ProductId(productId);

        // Iterate over each product image and delete it
        for (ProductImages productImage : productImagesList) {
            // Extract the public ID from the image URL
            String publicId = fileUtils.extractPublicIdFromUrl(productImage.getImageUrl());

            // Delete the file from Cloudinary
                fileUtils.deleteFileFromCloudinary(cloudinaryFolderName+"/"+publicId);

            // Delete the image record from the database
            productsImagesRepo.delete(productImage);
        }

        // Finally, delete the product itself from the repository
        productsRepo.deleteById(productId);
    }
}
