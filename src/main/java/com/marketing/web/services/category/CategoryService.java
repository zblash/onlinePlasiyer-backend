package com.marketing.web.services.category;

import com.marketing.web.dtos.category.WritableCategory;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.repositories.CategoryRepository;
import com.marketing.web.repositories.ProductRepository;
import com.marketing.web.utils.mappers.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findBySubCategory(boolean isSub) {
        return categoryRepository.findBySubCategory(isSub);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found with id: "+ id));
    }

    @Override
    public Category create(Category category) {
        if (category.isSubCategory() && category.getParent() != null){
            category.setParent(categoryRepository.findById(category.getParent().getId()).orElseThrow(() -> new ResourceNotFoundException("Parent Category not found with given parentId: "+ category.getParent().getId())));
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Long id, Category updatedCategory) {
        Category category = findById(id);
        if (updatedCategory.isSubCategory() && updatedCategory.getParent() != null){
            category.setParent(categoryRepository.findById(updatedCategory.getParent().getId()).orElseThrow(() -> new ResourceNotFoundException("Parent Category not found with given parentId: "+ updatedCategory.getParent().getId())));
        }
        if (updatedCategory.getPhotoUrl() != null && !updatedCategory.getPhotoUrl().isEmpty()){
            category.setPhotoUrl(updatedCategory.getPhotoUrl());
        }
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Category category) {
        List<Product> products = productRepository.findByCategoryIn(Arrays.asList(category));
        for (Product product:products){
            product.setCategory(null);
        }
        productRepository.saveAll(products);
        categoryRepository.delete(category);
    }

}
