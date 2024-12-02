package com.msp.everestFitness.service;

import com.msp.everestFitness.model.FeatureImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface FeatureImageService {
    void addFeatureImage(FeatureImage featureImage,MultipartFile image) throws IOException;
    List<FeatureImage> getAllFeatureImage();
    FeatureImage getFeatureImageById(UUID imageId);
    void deleteFeatureImageById(UUID imageId) throws IOException;

}
