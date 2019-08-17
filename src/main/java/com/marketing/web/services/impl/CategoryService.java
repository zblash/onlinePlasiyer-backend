package com.marketing.web.services.impl;

import com.marketing.web.dtos.CategoryDTO;
import com.marketing.web.models.Category;
import com.marketing.web.repositories.CategoryRepository;
import com.marketing.web.services.ICategoryService;
import com.marketing.web.utils.mappers.CategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    private Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Override
    public List<Category> findBaseCategories() {
        return categoryRepository.findBySubCategory(false);
    }

    @Override
    public List<Category> findSubCategories() {
        return categoryRepository.findBySubCategory(true);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public Category create(CategoryDTO categoryDTO) {
        Category category = CategoryMapper.INSTANCE.CategoryDTOtoCategory(categoryDTO);
        logger.info(Boolean.toString(categoryDTO.isSubCategory()));
        if (category.isSubCategory() && categoryDTO.getParentId() != null){
            category.setParent(categoryRepository.findById(categoryDTO.getParentId()).orElseThrow(RuntimeException::new));
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Category category, Category updatedCategory) {
        category.setName(updatedCategory.getName());
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Category category) {
        categoryRepository.delete(category);
    }

}
