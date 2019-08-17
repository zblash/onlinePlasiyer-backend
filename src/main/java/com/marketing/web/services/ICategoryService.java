package com.marketing.web.services;

import com.marketing.web.dtos.CategoryDTO;
import com.marketing.web.models.Category;
import java.util.List;
public interface ICategoryService {

    List<Category> findAll();

    Category findById(Long id);

    Category create(CategoryDTO categoryDTO);

    Category update(Category category, Category updatedCategory);

    void delete(Category category);

    List<Category> findBaseCategories();

    List<Category> findSubCategories();
}
