package com.msp.everestFitness.service;

import com.msp.everestFitness.model.Subcategory;

import java.util.List;
import java.util.UUID;

public interface SubcategoryService {
    void addSubCategory(Subcategory subcategory);
    List<Subcategory> getAllSubcategory();
    Subcategory getSubCategoryById(UUID id);
    List<Subcategory> findSubcategoriesByCategory(UUID category);
    void deleteSubCategory(UUID id);
}