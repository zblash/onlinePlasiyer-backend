package com.marketing.web.controllers;

import com.marketing.web.dtos.category.WritableCategory;
import com.marketing.web.models.Category;
import com.marketing.web.services.category.CategoryService;
import com.marketing.web.services.storage.StorageService;
import com.marketing.web.utils.mappers.CategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StorageService storageService;

    private Logger logger = LoggerFactory.getLogger(CategoriesController.class);

    @GetMapping
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false) boolean filter,
                                                 @RequestParam(required = false) boolean sub){
        if (filter){
           return ResponseEntity.ok(categoryService.findBySubCategory(sub));
        }
       return ResponseEntity.ok(categoryService.findAll());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@Valid WritableCategory writableCategory, @RequestParam(value="uploadfile", required = true) final MultipartFile uploadfile){
            String fileName = storageService.store(uploadfile);
            Category category = CategoryMapper.INSTANCE.writableCategorytoCategory(writableCategory);
            category.setPhotoUrl(fileName);
            category.setParent(categoryService.findById(writableCategory.getParentId()));
        return ResponseEntity.ok(categoryService.create(category));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public Map<String,Category> deleteCategory(@PathVariable(value = "id") Long id){
        Category category = categoryService.findById(id);
        categoryService.delete(category);
        Map<String,Category> response = new HashMap<>();
        response.put("deleted",category);
        return response;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable(value = "id") Long id, @Valid WritableCategory updatedCategory, @RequestParam(value="uploadfile", required = false) final MultipartFile uploadfile){
        Category category = CategoryMapper.INSTANCE.writableCategorytoCategory(updatedCategory);
        if (uploadfile != null && !uploadfile.isEmpty()) {
            String fileName = storageService.store(uploadfile);
            category.setPhotoUrl(fileName);
        }
        return ResponseEntity.ok(categoryService.update(id,category));
    }

}