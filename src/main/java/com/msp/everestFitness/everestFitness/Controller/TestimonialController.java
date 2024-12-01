package com.msp.everestFitness.everestFitness.Controller;

import com.msp.everestFitness.everestFitness.model.Testimonial;
import com.msp.everestFitness.everestFitness.service.TestimonialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/testimonial")
public class TestimonialController {
    @Autowired
    private TestimonialService testimonialService;

    @PostMapping("/")
    public ResponseEntity<?> addTestimonial(@ModelAttribute Testimonial testimonial, @RequestParam MultipartFile imageFile) throws IOException {
        testimonialService.addTestimonial(testimonial, imageFile);
        return new ResponseEntity<>("Testimonial created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getTestimonial(@RequestParam UUID testimonialId) {
        if (testimonialId != null) {
            return new ResponseEntity<>(testimonialService.getTestimonialById(testimonialId), HttpStatus.OK);
        }
        return new ResponseEntity<>(testimonialService.getAllTestimonial(), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteTestimonialById(@RequestParam UUID testimonialId) throws IOException {
        testimonialService.deleteTestimonialByID(testimonialId);
        return new ResponseEntity<>("The testimonial deleted successfully", HttpStatus.OK);
    }
}
