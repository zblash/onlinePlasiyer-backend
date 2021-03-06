package com.marketing.web.controllers;

import com.marketing.web.dtos.category.ReadableCategory;
import com.marketing.web.dtos.category.WritableCategory;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.services.category.CategoryService;
import com.marketing.web.services.product.ProductService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.storage.AmazonClient;
import com.marketing.web.utils.mappers.CategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/private/categories")
public class CategoriesController {

    private final CategoryService categoryService;

    private final AmazonClient amazonClient;

    private final ProductService productService;

    private final ProductSpecifyService productSpecifyService;

    private Logger logger = LoggerFactory.getLogger(CategoriesController.class);

    public CategoriesController(CategoryService categoryService, AmazonClient amazonClient, ProductService productService, ProductSpecifyService productSpecifyService) {
        this.categoryService = categoryService;
        this.amazonClient = amazonClient;
        this.productService = productService;
        this.productSpecifyService = productSpecifyService;
    }


    @GetMapping
    public ResponseEntity<List<ReadableCategory>> getAll(@RequestParam(required = false) boolean filter,
                                                         @RequestParam(required = false) boolean sub) {
        List<Category> categories;
        if (filter) {
            categories = categoryService.findBySubCategory(sub);
        } else {
            categories = categoryService.findAll();
        }
        return ResponseEntity.ok(categories.stream()
                .map(CategoryMapper::categoryToReadableCategory).collect(Collectors.toList()));
    }

    @GetMapping("/{id}/subCategories")
    public ResponseEntity<List<ReadableCategory>> getSubCategoriesById(@PathVariable String id) {
        Category category = categoryService.findById(id);
        if (!category.isSubCategory()) {
            return ResponseEntity.ok(category.getChilds().stream()
                    .map(CategoryMapper::categoryToReadableCategory).collect(Collectors.toList()));
        }
        throw new BadRequestException("This category is not main: " + id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadableCategory> findByUUID(@PathVariable String id) {
        return ResponseEntity.ok(CategoryMapper.categoryToReadableCategory(categoryService.findById(id)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ReadableCategory> createCategory(@Valid WritableCategory writableCategory, @RequestParam MultipartFile uploadfile) {
        if (writableCategory.getCommission() == null) {
            throw new BadRequestException("Commission can not null");
        }
        Category category = CategoryMapper.writableCategorytoCategory(writableCategory);
        String fileUrl = amazonClient.uploadFile(uploadfile);
        category.setPhotoUrl(fileUrl);

        if (category.isSubCategory()) {
            category.setParent(categoryService.findById(writableCategory.getParentId()));
        }
        Category savedCategory = categoryService.create(category);

        return new ResponseEntity<>(CategoryMapper.categoryToReadableCategory(savedCategory), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableCategory> deleteCategory(@PathVariable String id) {
        Category category = categoryService.findById(id);
        amazonClient.deleteFileFromS3Bucket(category.getPhotoUrl());
        categoryService.delete(category);
        return ResponseEntity.ok(CategoryMapper.categoryToReadableCategory(category));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableCategory> updateCategory(@PathVariable String id, @Valid WritableCategory updatedCategory, @RequestParam(value = "uploadfile", required = false) MultipartFile uploadfile) {
        Category category = CategoryMapper.writableCategorytoCategory(updatedCategory);

        if (uploadfile != null && !uploadfile.isEmpty()) {
            amazonClient.deleteFileFromS3Bucket(category.getPhotoUrl());
            String fileUrl = amazonClient.uploadFile(uploadfile);
            category.setPhotoUrl(fileUrl);
        }
        if (category.isSubCategory()) {
            category.setParent(categoryService.findById(updatedCategory.getParentId()));
        }
        category = categoryService.update(id, category);
        if (category.getCommission() > 0) {
            List<Product> products = productService.findAllByCategoryId(category.getId().toString()).stream().peek(product -> product.setCommission(updatedCategory.getCommission())).collect(Collectors.toList());
            List<ProductSpecify> productSpecifies = products.stream()
                    .map(Product::getProductSpecifies)
                    .flatMap(Collection::stream)
                    .peek(productSpecify -> productSpecify.setCommission(updatedCategory.getCommission()))
                    .collect(Collectors.toList());
            productService.saveAll(products);
            productSpecifyService.updateAll(productSpecifies);
        }
        return ResponseEntity.ok(CategoryMapper.categoryToReadableCategory(category));
    }

}