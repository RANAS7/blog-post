package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.Category;
import com.msp.everestFitness.everestFitness.repository.CategoryRepo;
import com.msp.everestFitness.everestFitness.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    @Override
    public void addCategory(Category category) {
        if (category.getCategoryId() != null) {
            Category category1 = categoryRepo.findById(category.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + category.getCategoryId()));

            category1.setName(category.getName());
            category1.setUpdatedAt(Timestamp.from(Instant.now()));
            categoryRepo.save(category1);
            return;
        }
        categoryRepo.save(category);
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    @Override
    public void deleteCategory(UUID id) {
        if (id == null) {
            throw new ResourceNotFoundException("The Category is not exist in ur record");
        }
        categoryRepo.deleteById(id);
    }
}
