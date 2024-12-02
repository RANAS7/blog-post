package com.msp.everestFitness.service.impl;

import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.model.Subcategory;
import com.msp.everestFitness.repository.SubcategoryRepo;
import com.msp.everestFitness.service.SubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {
    @Autowired
    private SubcategoryRepo subcategoryRepo;


    @Override
    public void addSubCategory(Subcategory subcategory) {
        if (subcategory.getSubcategoryId() != null) {
            Subcategory subcategory1 = subcategoryRepo.findById(subcategory.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with the provided ID: " + subcategory.getSubcategoryId()));

            subcategory1.setName(subcategory.getName());
            subcategory1.setCategory(subcategory.getCategory());
            subcategory1.setUpdatedAt(Timestamp.from(Instant.now()));

            subcategoryRepo.save(subcategory1);
        }
        subcategoryRepo.save(subcategory);
    }

    @Override
    public List<Subcategory> getAllSubcategory() {
        return subcategoryRepo.findAll();
    }

    @Override
    public Subcategory getSubCategoryById(UUID id) {
        return subcategoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with the provided ID: " + id));
    }

    @Override
    public List<Subcategory> findSubcategoriesByCategory(UUID category) {
        return subcategoryRepo.findByCategory_CategoryId(category);
    }

    @Override
    public void deleteSubCategory(UUID id) {
        if (id == null) {
            throw new ResourceNotFoundException("The Subcategory is not exist in our record");
        }
        subcategoryRepo.deleteById(id);
    }
}
