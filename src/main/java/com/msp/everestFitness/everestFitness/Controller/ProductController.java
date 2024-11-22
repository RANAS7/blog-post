package com.msp.everestFitness.everestFitness.Controller;

import com.msp.everestFitness.everestFitness.model.Products;
import com.msp.everestFitness.everestFitness.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = "/", consumes = "multipart/form-data")
    public ResponseEntity<?> addProduct(
            @ModelAttribute Products products,
            @RequestParam UUID subcategoryId,
            @RequestPart("images") List<MultipartFile> images) throws IOException {

        productService.addProduct(products, subcategoryId, images);
        return new ResponseEntity<>("Product added successfully", HttpStatus.CREATED);
    }


    @GetMapping("/")
    public ResponseEntity<?> getProducts(@RequestParam(name = "productId", required = false) UUID productId) {
        if (productId != null) {
            return new ResponseEntity<>(productService.getProductByIdWithImages(productId), HttpStatus.OK);
        }
        return new ResponseEntity<>(productService.getAllProductsWithImages(), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteProductById(@RequestParam UUID productId) throws IOException {
        productService.deleteProductById(productId);
        return new ResponseEntity<>("Product successfully deleted", HttpStatus.OK);
    }

    @GetMapping("/suggestions/by-subcategory")
    public ResponseEntity<?> getSuggestedProductsBySubcategory(@RequestParam UUID subcategoryId) {
        return new ResponseEntity<>(productService.getSuggestedProductsBySubcategory(subcategoryId), HttpStatus.OK);
    }

    @GetMapping("/suggestions/by-discount")
    public ResponseEntity<?> getSuggestedProductsByDiscount(@RequestParam double minDiscount) {
        return new ResponseEntity<>(productService.getSuggestedProductsByDiscount(minDiscount), HttpStatus.OK);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<?> getAllSuggestedProducts(@RequestParam UUID subcategoryId,@RequestParam double minDiscount){
        return new ResponseEntity<>(productService.getSuggestedProducts(subcategoryId, minDiscount),HttpStatus.OK);
    }
}
