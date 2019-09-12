package com.marketing.web.services.category;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.repositories.CategoryRepository;
import com.marketing.web.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

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

    public Category findByUUID(String uuid) {
        return categoryRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Category not found with id: "+ uuid));
    }

    @Override
    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category update(String id, Category updatedCategory) {
        Category category = findByUUID(id);
        if (updatedCategory.isSubCategory() && updatedCategory.getParent() != null){
            category.setParent(updatedCategory.getParent());
            category.setSubCategory(updatedCategory.isSubCategory());
        }
        if (updatedCategory.getPhotoUrl() != null && !updatedCategory.getPhotoUrl().isEmpty()){
            category.setPhotoUrl(updatedCategory.getPhotoUrl());
        }
        category.setName(updatedCategory.getName());
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
