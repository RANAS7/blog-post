package com.msp.everestFitness.everestFitness.restController;

import com.msp.everestFitness.everestFitness.model.Category;
import com.msp.everestFitness.everestFitness.model.Subcategory;
import com.msp.everestFitness.everestFitness.service.SubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subcategory")
public class SubcategoryController {
    @Autowired
    private SubcategoryService subcategoryService;

    @PostMapping("/")
    public ResponseEntity<?> addSubcategory(@RequestBody Subcategory subcategory) {
        subcategoryService.addSubCategory(subcategory);
        return new ResponseEntity<>("Subcategory Successfully created", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getSubcategory(@RequestParam(name = "id", required = false) UUID id) {
        if (id != null) {
            return new ResponseEntity<>(subcategoryService.getSubCategoryById(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(subcategoryService.getAllSubcategory(), HttpStatus.OK);
    }

    @GetMapping("/by-category")
    public ResponseEntity<?> getSubcategoryByCategory(@RequestParam Category category) {
        return new ResponseEntity<>(subcategoryService.findSubcategoriesByCategory(category), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteSubcategory(@RequestParam UUID id) {
        subcategoryService.deleteSubCategory(id);
        return new ResponseEntity<>("Subcategory deleted successfully", HttpStatus.OK);
    }
}
