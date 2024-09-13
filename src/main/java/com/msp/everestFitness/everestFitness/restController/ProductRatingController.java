package com.msp.everestFitness.everestFitness.restController;

import com.msp.everestFitness.everestFitness.dto.ProductRatingRequestDto;
import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.service.ProductRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/product-ratings")
public class ProductRatingController {

    @Autowired
    private ProductRatingService productRatingService;

    // Add a new rating
    @PostMapping("/")
    public ResponseEntity<?> addRating(@RequestBody ProductRatingRequestDto request) {
        productRatingService.addRating(request);
        return new ResponseEntity<>("Thank you for rate products", HttpStatus.CREATED);
    }

    // Get all ratings for a product
    @GetMapping("/")
    public ResponseEntity<?> getRatingsForProduct(@RequestParam Products product) {
        return new ResponseEntity<>(productRatingService.getRatingsForProduct(product), HttpStatus.OK);
    }


    // Delete a rating
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRating(@RequestParam UUID ratingId, @RequestParam UUID userId) {
        productRatingService.deleteRating(ratingId, userId);
        return new ResponseEntity<>("Rating deleted successfully", HttpStatus.OK);
    }

    // Get avg ratings of a product
    @GetMapping("/avg")
    public ResponseEntity<?> getAverageRatingByProductId(@RequestParam UUID productId) {
        return new ResponseEntity<>(productRatingService.getAverageRatingByProductId(productId), HttpStatus.OK);
    }
}
