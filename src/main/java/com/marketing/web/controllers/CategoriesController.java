package com.marketing.web.controllers;

import com.marketing.web.dtos.CategoryDTO;
import com.marketing.web.models.Category;
import com.marketing.web.models.CustomPrincipal;
import com.marketing.web.services.impl.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    private Logger logger = LoggerFactory.getLogger(CategoriesController.class);

    @GetMapping
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false) boolean filter,
                                                 @RequestParam(required = false) boolean sub){
        if (filter){
           return ResponseEntity.ok(categoryService.findBySubCategory(sub));
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal pricipal = (CustomPrincipal) auth.getPrincipal();
        logger.info(pricipal.getUser().getName());
       return ResponseEntity.ok(categoryService.findAll());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        return ResponseEntity.ok(categoryService.create(categoryDTO));
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
    public ResponseEntity<Category> updateCategory(@PathVariable(value = "id") Long id,@Valid @RequestBody Category updatedCategory){
        return ResponseEntity.ok(categoryService.update(categoryService.findById(id),updatedCategory));
    }


}