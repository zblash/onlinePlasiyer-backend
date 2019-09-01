package com.marketing.web.services.category;

import com.marketing.web.dtos.category.CategoryDTO;
import com.marketing.web.models.Category;
import java.util.List;
public interface ICategoryService {

    List<Category> findAll();

    List<Category> findBySubCategory(boolean isSub);

    Category findById(Long id);

    Category create(CategoryDTO categoryDTO);

    Category update(Category category, CategoryDTO updatedCategoryDTO);

    void delete(Category category);

}