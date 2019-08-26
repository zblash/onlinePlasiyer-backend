package com.marketing.web.controllers.admin;

import com.marketing.web.dtos.CategoryDTO;
import com.marketing.web.models.Category;
import com.marketing.web.services.impl.CategoryService;
import com.marketing.web.services.impl.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminCategoriesController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public ResponseEntity<List<Category>> getAll(){
        return ResponseEntity.ok(categoryService.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@Valid CategoryDTO categoryDTO, @RequestParam(value="uploadfile", required = true) final MultipartFile uploadfile){
       if (!uploadfile.isEmpty()) {
           String fileName = storageService.store(uploadfile);
           categoryDTO.setPhotoUrl(fileName);
       }
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
    public ResponseEntity<Category> updateCategory(@PathVariable(value = "id") Long id,@Valid CategoryDTO updatedCategory, @RequestParam(value="uploadfile", required = false) final MultipartFile uploadfile){
        if (uploadfile != null && !uploadfile.isEmpty()) {
            String fileName = storageService.store(uploadfile);
            updatedCategory.setPhotoUrl(fileName);
        }
        return ResponseEntity.ok(categoryService.update(categoryService.findById(id),updatedCategory));
    }
}
