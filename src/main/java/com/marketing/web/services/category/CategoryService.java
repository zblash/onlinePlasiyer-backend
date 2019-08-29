package com.marketing.web.services.category;

import com.marketing.web.dtos.category.CategoryDTO;
import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.repositories.CategoryRepository;
import com.marketing.web.repositories.ProductRepository;
import com.marketing.web.services.category.ICategoryService;
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
        return categoryRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public Category create(CategoryDTO categoryDTO) {
        Category category = CategoryMapper.INSTANCE.CategoryDTOtoCategory(categoryDTO);
        if (category.isSubCategory() && categoryDTO.getParentId() != null){
            category.setParent(categoryRepository.findById(categoryDTO.getParentId()).orElseThrow(RuntimeException::new));
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Category category, CategoryDTO updatedCategoryDTO) {
        Category updatedCategory = CategoryMapper.INSTANCE.CategoryDTOtoCategory(updatedCategoryDTO);
        category.setName(updatedCategory.getName());
        if (updatedCategory.isSubCategory() && updatedCategoryDTO.getParentId() != null){
            category.setParent(categoryRepository.findById(updatedCategoryDTO.getParentId()).orElseThrow(RuntimeException::new));
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
