package com.marketing.web.controllers;

import com.marketing.web.models.Product;
import com.marketing.web.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll(){
        return ResponseEntity.ok(productService.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Product> createPost(@Valid @RequestBody Product category){
        return ResponseEntity.ok(productService.create(category));
    }

    @DeleteMapping("/delete/{id}")
    public Map<String,Product> deleteCategory(@PathVariable(value = "id") Long id){
        Product product = productService.findById(id);
        productService.delete(product);
        Map<String,Product> response = new HashMap<>();
        response.put("deleted",product);
        return response;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateCategory(@PathVariable(value = "id") Long id,@Valid @RequestBody Product updatedProduct){
        Product product = productService.findById(id);
        return ResponseEntity.ok(productService.update(updatedProduct));
    }

}
