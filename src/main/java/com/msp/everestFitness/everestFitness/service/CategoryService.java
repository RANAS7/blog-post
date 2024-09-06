package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
void addCategory(Category category);
Category getCategoryById(UUID id);
List<Category> getAllCategories();
void deleteCategory(UUID id);
}
