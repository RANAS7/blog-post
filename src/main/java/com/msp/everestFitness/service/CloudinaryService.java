package com.msp.everestFitness.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    public Map upload(MultipartFile file);
}
