package com.msp.everestFitness.everestFitness.restController;

import com.msp.everestFitness.everestFitness.model.Category;
import com.msp.everestFitness.everestFitness.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/")
    public ResponseEntity<?> addCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
        return new ResponseEntity<>("Category Added Successfully", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getCategory(@RequestParam(name = "id", required = false) UUID id) {
        if (id != null) {
            return new ResponseEntity<>(categoryService.getCategoryById(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteCategory(@RequestParam UUID id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>("The Category SuccessFully Deleted", HttpStatus.OK);
    }

}
