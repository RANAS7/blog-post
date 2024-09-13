package com.msp.everestFitness.everestFitness.restController;

import com.msp.everestFitness.everestFitness.model.FeatureImage;
import com.msp.everestFitness.everestFitness.service.FeatureImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/feature-image")
public class FeatureImageController {
    @Autowired
    private FeatureImageService featureImageService;

    @PostMapping("/")
    public ResponseEntity<?> addFeatureImage(@RequestAttribute FeatureImage featureImage,
                                             @RequestParam MultipartFile image) throws IOException {
        featureImageService.addFeatureImage(featureImage, image);
        return new ResponseEntity<>("Feature image added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getFeatureImage(@RequestParam UUID imageId) {
        if (imageId != null) {
            return new ResponseEntity<>(featureImageService.getFeatureImageById(imageId), HttpStatus.OK);
        }
        return new ResponseEntity<>(featureImageService.getAllFeatureImage(), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteFeatureImage(@RequestParam UUID imageId) throws IOException {
        featureImageService.deleteFeatureImageById(imageId);
        return new ResponseEntity<>("Feature image deleted successfully", HttpStatus.OK);
    }
}