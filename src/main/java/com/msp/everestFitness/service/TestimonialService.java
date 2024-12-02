package com.msp.everestFitness.service;

import com.msp.everestFitness.model.Testimonial;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TestimonialService {
    void addTestimonial(Testimonial testimonial, MultipartFile imageFile) throws IOException;
    List<Testimonial> getAllTestimonial();
    Testimonial getTestimonialById(UUID testimonialId);
    void deleteTestimonialByID(UUID testimonialId) throws IOException;

}
