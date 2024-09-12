package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.FeatureImage;
import com.msp.everestFitness.everestFitness.repository.FeatureImageRepo;
import com.msp.everestFitness.everestFitness.service.FeatureImageService;
import com.msp.everestFitness.everestFitness.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FeatureImageServiceImpl implements FeatureImageService {
    @Autowired
    private FeatureImageRepo featureImageRepo;

    @Autowired
    private FileUtils fileUtils;

    @Value("${CLOUDINARY_FOLDER_NAME}")
    private String cloudinaryFolderName;

    @Override
    public void addFeatureImage(FeatureImage featureImage, MultipartFile image) throws IOException {
        String imageUrl = fileUtils.generateFileName(image);

        if (featureImage.getFeatureId() != null) {
            FeatureImage featureImage1 = featureImageRepo
                    .findById(featureImage.getFeatureId()).orElseThrow(() -> new ResourceNotFoundException("The feature image not found with the Id: " + featureImage.getFeatureId()));

            featureImage1.setFileUrl(imageUrl);
            featureImageRepo.save(featureImage1);

            fileUtils.uploadFileToCloudinary(image);
        }

        featureImage.setFileUrl(imageUrl);
        featureImageRepo.save(featureImage);

        fileUtils.uploadFileToCloudinary(image);
    }

    @Override
    public List<FeatureImage> getAllFeatureImage() {
        return featureImageRepo.findAll();
    }

    @Override
    public FeatureImage getFeatureImageById(UUID imageId) {
        return featureImageRepo.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("The feature image not found with the feature image id: " + imageId));
    }

    @Override
    public void deleteFeatureImageById(UUID imageId) throws IOException {
        featureImageRepo.deleteById(imageId);

        FeatureImage featureImage = featureImageRepo.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("The feature image not found with the feature image id: " + imageId));

        String publicID = fileUtils.extractPublicIdFromUrl(featureImage.getFileUrl());

        fileUtils.deleteFileFromCloudinary(cloudinaryFolderName + "/" + publicID);
    }
}