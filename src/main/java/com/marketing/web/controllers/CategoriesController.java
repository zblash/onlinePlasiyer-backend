package com.marketing.web.controllers;

import com.marketing.web.dtos.category.ReadableCategory;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StorageService storageService;

    private Logger logger = LoggerFactory.getLogger(CategoriesController.class);

    @GetMapping
    public ResponseEntity<List<ReadableCategory>> getAll(@RequestParam(required = false) boolean filter,
                                                 @RequestParam(required = false) boolean sub){
        List<Category> categories;
        if (filter){
            categories = categoryService.findBySubCategory(sub);
        }else{
            categories = categoryService.findAll();
        }
       return ResponseEntity.ok(categories.stream()
               .map(CategoryMapper.INSTANCE::categoryToReadableCategory).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ReadableCategory> createCategory(@Valid WritableCategory writableCategory, @RequestParam(value="uploadfile", required = true) final MultipartFile uploadfile){
            Category category = CategoryMapper.INSTANCE.writableCategorytoCategory(writableCategory);
            String fileName = storageService.store(uploadfile);
            category.setPhotoUrl(fileName);

            Category savedCategory = categoryService.create(category);

        return ResponseEntity.ok(CategoryMapper.INSTANCE.categoryToReadableCategory(savedCategory));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public Map<String,ReadableCategory> deleteCategory(@PathVariable(value = "id") Long id){
        Category category = categoryService.findById(id);
        categoryService.delete(category);
        Map<String,ReadableCategory> response = new HashMap<>();
        response.put("deleted",CategoryMapper.INSTANCE.categoryToReadableCategory(category));
        return response;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ReadableCategory> updateCategory(@PathVariable(value = "id") Long id, @Valid WritableCategory updatedCategory, @RequestParam(value="uploadfile", required = false) final MultipartFile uploadfile){
        Category category = CategoryMapper.INSTANCE.writableCategorytoCategory(updatedCategory);
        if (uploadfile != null && !uploadfile.isEmpty()) {
            String fileName = storageService.store(uploadfile);
            category.setPhotoUrl(fileName);
        }
        return ResponseEntity.ok(CategoryMapper.INSTANCE.categoryToReadableCategory(categoryService.update(id,category)));
    }

}