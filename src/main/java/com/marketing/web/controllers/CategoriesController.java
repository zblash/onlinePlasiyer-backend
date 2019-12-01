package com.marketing.web.controllers;

import com.marketing.web.dtos.category.ReadableCategory;
import com.marketing.web.dtos.category.WritableCategory;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.Category;
import com.marketing.web.services.category.CategoryService;
import com.marketing.web.services.storage.AmazonClient;
import com.marketing.web.utils.mappers.CategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AmazonClient amazonClient;

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
               .map(CategoryMapper::categoryToReadableCategory).collect(Collectors.toList()));
    }

    @GetMapping("/{id}/subCategories")
    public ResponseEntity<List<ReadableCategory>> getAllSubCategories(@PathVariable String id){
        Category category = categoryService.findByUUID(id);
        if (!category.isSubCategory()){
            return ResponseEntity.ok(category.getChilds().stream()
                    .map(CategoryMapper::categoryToReadableCategory).collect(Collectors.toList()));
        }
        throw new BadRequestException("This category is not main: "+id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadableCategory> findByUUID(@PathVariable String id){
        return ResponseEntity.ok(CategoryMapper.categoryToReadableCategory(categoryService.findByUUID(id)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ReadableCategory> createCategory(@Valid WritableCategory writableCategory, @RequestParam MultipartFile uploadfile){
            Category category = CategoryMapper.writableCategorytoCategory(writableCategory);
            String fileUrl = amazonClient.uploadFile(uploadfile);
            category.setPhotoUrl(fileUrl);

            if (category.isSubCategory()){
                category.setParent(categoryService.findByUUID(writableCategory.getParentId()));
            }

            Category savedCategory = categoryService.create(category);

        return ResponseEntity.ok(CategoryMapper.categoryToReadableCategory(savedCategory));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ReadableCategory> deleteCategory(@PathVariable String id){
        Category category = categoryService.findByUUID(id);
        amazonClient.deleteFileFromS3Bucket(category.getPhotoUrl());
        categoryService.delete(category);
        return ResponseEntity.ok(CategoryMapper.categoryToReadableCategory(category));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ReadableCategory> updateCategory(@PathVariable String id, @Valid WritableCategory updatedCategory, @RequestParam(value="uploadfile", required = false) final MultipartFile uploadfile){
        Category category = CategoryMapper.writableCategorytoCategory(updatedCategory);
        if (uploadfile != null && !uploadfile.isEmpty()) {
            amazonClient.deleteFileFromS3Bucket(category.getPhotoUrl());
            String fileUrl = amazonClient.uploadFile(uploadfile);
            category.setPhotoUrl(fileUrl);
        }
        if (category.isSubCategory()){
            category.setParent(categoryService.findByUUID(updatedCategory.getParentId()));
        }
        return ResponseEntity.ok(CategoryMapper.categoryToReadableCategory(categoryService.update(id,category)));
    }

}