package com.msp.everestFitness.everestFitness.service.impl;

import com.cloudinary.Cloudinary;
import com.msp.everestFitness.everestFitness.dto.ProductWithImagesDto;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.ProductImages;
import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.model.Subcategory;
import com.msp.everestFitness.everestFitness.repository.*;
import com.msp.everestFitness.everestFitness.service.ProductService;
import com.msp.everestFitness.everestFitness.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.io.IOException;
import java.util.*;
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

    @Autowired
    private ProductRatingRepo productRatingRepo;

    @Autowired
    private OrderItemsRepo orderItemsRepo;

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
        if (images.size() + images.size() > 5) {
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
            dto.setRating(productRatingRepo.getAverageRatingByProductId(product.getProductId()));

            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public ProductWithImagesDto getProductByIdWithImages(UUID productId) {
        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " was not found."));

        List<String> imageUrls = productsImagesRepo.findByProduct_ProductId(productId)
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
        dto.setRating(productRatingRepo.getAverageRatingByProductId(productId));

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
            fileUtils.deleteFileFromCloudinary(cloudinaryFolderName + "/" + publicId);

            // Delete the image record from the database
            productsImagesRepo.delete(productImage);
        }

        // Delete the product itself from the repository
        productsRepo.deleteById(productId);
    }

    // Suggest products based on subcategory
    @Override
    public List<ProductWithImagesDto> getSuggestedProductsBySubcategory(UUID subcategoryId) {
        // Fetch products by subcategory
        List<Products> productsList = productsRepo.findBySubcategory_SubcategoryId(subcategoryId);
        List<ProductWithImagesDto> productWithImagesList = new ArrayList<>();

        // Add images to each product DTO
        for (Products product : productsList) {
            // Fetch images for the current product
            List<String> imageUrls = productsImagesRepo.findByProduct_ProductId(product.getProductId())
                    .stream()
                    .map(ProductImages::getImageUrl)
                    .collect(Collectors.toList());

            // Create a DTO for the product
            ProductWithImagesDto dto = new ProductWithImagesDto();
            dto.setProductId(product.getProductId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setDiscountedPrice(product.getDiscountedPrice());
            dto.setImageUrls(imageUrls);
            dto.setRating(productRatingRepo.getAverageRatingByProductId(product.getProductId()));

            // Add the DTO to the result list
            productWithImagesList.add(dto);
        }

        return productWithImagesList; // Return the list of DTOs
    }


    // Suggest products based on discounted price
    @Override
    public List<ProductWithImagesDto> getSuggestedProductsByDiscount(double minDiscount) {
        List<Products> productsList = productsRepo.findByDiscountedPriceGreaterThan(minDiscount);
        List<ProductWithImagesDto> productWithImagesList = new ArrayList<>();


        // Add images to each product DTO
        for (Products product : productsList) {
            List<String> imageUrls = productsImagesRepo.findByProduct_ProductId(product.getProductId())
                    .stream()
                    .map(ProductImages::getImageUrl)
                    .collect(Collectors.toList());

            // Create a DTO for the product
            ProductWithImagesDto dto = new ProductWithImagesDto();
            dto.setProductId(product.getProductId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setDiscountedPrice(product.getDiscountedPrice());
            dto.setImageUrls(imageUrls);
            dto.setRating(productRatingRepo.getAverageRatingByProductId(product.getProductId()));

            // Add the DTO to the result list
            productWithImagesList.add(dto);
        }

        return productWithImagesList;
    }


    @Override
    public List<ProductWithImagesDto> getSuggestedProductsByCategory(UUID categoryId) {
        // Fetch subcategories for the given category
        List<Subcategory> subcategoryList = subcategoryRepo.findByCategory_CategoryId(categoryId);

        // Initialize the list to hold the product DTOs
        List<ProductWithImagesDto> suggestedProducts = new ArrayList<>();

        // Loop through each subcategory to get products
        for (Subcategory subcategory : subcategoryList) {
            List<Products> productWithImagesList = productsRepo.findBySubcategory_SubcategoryId(subcategory.getSubcategoryId());

            // For each product in the subcategory, gather images and other details
            for (Products product : productWithImagesList) {
                // Fetch product images
                List<String> imageUrls = productsImagesRepo.findByProduct_ProductId(product.getProductId())
                        .stream()
                        .map(ProductImages::getImageUrl)
                        .collect(Collectors.toList());

                // Create a new ProductWithImagesDto and set its properties
                ProductWithImagesDto dto = new ProductWithImagesDto();
                dto.setProductId(product.getProductId());
                dto.setName(product.getName());
                dto.setDescription(product.getDescription());
                dto.setPrice(product.getPrice());
                dto.setDiscountedPrice(product.getDiscountedPrice());
                dto.setImageUrls(imageUrls);

                // Fetch the product rating
                Double averageRating = productRatingRepo.getAverageRatingByProductId(product.getProductId());
                dto.setRating(averageRating != null ? averageRating : 0.0);

                // Add the DTO to the result list
                suggestedProducts.add(dto);
            }
        }

        // Return the list of suggested products
        return suggestedProducts;
    }


    @Override
    public List<ProductWithImagesDto> getSuggestedProducts(UUID subcategoryId, double minDiscount) {
        // Fetch products based on subcategory and discount
        List<Products> productsBySubcategory = productsRepo.findBySubcategory_SubcategoryId(subcategoryId);
        List<Products> productsByDiscount = productsRepo.findByDiscountedPriceGreaterThan(minDiscount);

        // Use a Set to avoid duplicates while combining products
        Set<UUID> productIds = new HashSet<>();
        List<ProductWithImagesDto> suggestedProducts = new ArrayList<>();

        // Add products from the subcategory
        for (Products product : productsBySubcategory) {
            ProductWithImagesDto dto = mapToDto(product);
            suggestedProducts.add(dto);
            productIds.add(product.getProductId());
        }

        // Add products from the discount list, avoiding duplicates
        for (Products product : productsByDiscount) {
            if (!productIds.contains(product.getProductId())) {
                ProductWithImagesDto dto = mapToDto(product);
                suggestedProducts.add(dto);
            }
        }

        // Add images and ratings to each product DTO
        for (ProductWithImagesDto product : suggestedProducts) {
            List<String> imageUrls = productsImagesRepo.findByProduct_ProductId(product.getProductId())
                    .stream()
                    .map(ProductImages::getImageUrl)
                    .collect(Collectors.toList());
            product.setImageUrls(imageUrls);

            // Fetch and set the rating
            Double averageRating = productRatingRepo.getAverageRatingByProductId(product.getProductId());
            product.setRating(averageRating != null ? averageRating : 0.0);
        }

        return suggestedProducts;
    }

    // Helper method to map Products to ProductWithImagesDto
    private ProductWithImagesDto mapToDto(Products product) {
        ProductWithImagesDto dto = new ProductWithImagesDto();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountedPrice(product.getDiscountedPrice());
        return dto;
    }


    // Find the top 10 popular products based on total quantity sold
    @Override
    public List<ProductWithImagesDto> getPopularProducts() {
        // Define pagination to limit the results to 10
        Pageable top10 = (Pageable) PageRequest.of(0, 10);

        // Fetch 10 popular products by quantity sold
        List<Object[]> popularProductsData = orderItemsRepo.findPopularProducts(top10);
        List<ProductWithImagesDto> popularProducts = new ArrayList<>();

        for (Object[] row : popularProductsData) {
            Products product = (Products) row[0];
            Long totalQuantitySold = (Long) row[1];

            // Fetch images for the product
            List<String> imageUrls = productsImagesRepo.findByProduct_ProductId(product.getProductId())
                    .stream()
                    .map(ProductImages::getImageUrl)
                    .collect(Collectors.toList());

            // Create ProductWithImagesDto
            ProductWithImagesDto dto = new ProductWithImagesDto();
            dto.setProductId(product.getProductId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setDiscountedPrice(product.getDiscountedPrice());
            dto.setImageUrls(imageUrls);
            popularProducts.add(dto);
        }

        return popularProducts;
    }
}
