package com.msp.everestFitness.service.impl;

import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.model.Testimonial;
import com.msp.everestFitness.repository.TestimonialRepo;
import com.msp.everestFitness.service.TestimonialService;
import com.msp.everestFitness.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class TestimonialServiceImpl implements TestimonialService {

    @Autowired
    private TestimonialRepo testimonialRepo;

    @Autowired
    private FileUtils fileUtils;

    @Value("${CLOUDINARY_FOLDER_NAME}")
    private String cloudinaryFolderName;

    @Override
    public void addTestimonial(Testimonial testimonial, MultipartFile imageFile) throws IOException {

        String fileName = fileUtils.uploadFileToCloudinary(imageFile);

        if (testimonial.getTestimonialId() != null) {
            Testimonial testimonial1 = testimonialRepo.findById(testimonial.getTestimonialId())
                    .orElseThrow(() -> new ResourceNotFoundException("The testimonial not found with the id: " + testimonial.getTestimonialId()));
            testimonial1.setPosition(testimonial.getPosition());
            testimonial1.setName(testimonial.getName());
            testimonial1.setImage(fileName);
            testimonial1.setDescription(testimonial.getDescription());

            testimonialRepo.save(testimonial1);

//       Save file into cloudinary
            fileUtils.uploadFileToCloudinary(imageFile);
        }

        testimonial.setImage(fileName);
        testimonialRepo.save(testimonial);

        //       Save file into cloudinary
        fileUtils.uploadFileToCloudinary(imageFile);
    }

    @Override
    public List<Testimonial> getAllTestimonial() {
        return testimonialRepo.findAll();
    }

    @Override
    public Testimonial getTestimonialById(UUID testimonialId) {
        return testimonialRepo.findById(testimonialId)
                .orElseThrow(() -> new ResourceNotFoundException("The testimonial not found with the id: " + testimonialId));
    }

    @Override
    public void deleteTestimonialByID(UUID testimonialId) throws IOException {
        Testimonial testimonial = testimonialRepo.findById(testimonialId)
                .orElseThrow(() -> new ResourceNotFoundException("The testimonial not found with the id: " + testimonialId));

        String publicId = fileUtils.extractPublicIdFromUrl(testimonial.getImage());
        fileUtils.deleteFileFromCloudinary(cloudinaryFolderName + "/" + publicId);

        testimonialRepo.deleteById(testimonialId);


    }
}
