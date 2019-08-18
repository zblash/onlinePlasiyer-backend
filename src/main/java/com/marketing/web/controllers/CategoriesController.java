package com.marketing.web.controllers;

import com.marketing.web.dtos.CategoryDTO;
import com.marketing.web.models.Category;
import com.marketing.web.services.impl.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false) boolean filter,
                                                 @RequestParam(required = false) boolean sub){
        if (filter){
           return ResponseEntity.ok(categoryService.findBySubCategory(sub));
        }
       return ResponseEntity.ok(categoryService.findAll());
    }


    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        return ResponseEntity.ok(categoryService.create(categoryDTO));
    }

    @DeleteMapping("/delete/{id}")
    public Map<String,Category> deleteCategory(@PathVariable(value = "id") Long id){
        Category category = categoryService.findById(id);
        categoryService.delete(category);
        Map<String,Category> response = new HashMap<>();
        response.put("deleted",category);
        return response;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable(value = "id") Long id,@Valid @RequestBody Category updatedCategory){
        return ResponseEntity.ok(categoryService.update(categoryService.findById(id),updatedCategory));
    }


}