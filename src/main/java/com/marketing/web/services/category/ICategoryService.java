package com.marketing.web.services.category;

import com.marketing.web.dtos.category.WritableCategory;
import com.marketing.web.models.Category;
import java.util.List;
public interface ICategoryService {

    List<Category> findAll();

    List<Category> findBySubCategory(boolean isSub);

    Category findById(Long id);

    Category create(Category category);

    Category update(Long id, Category updatedCategory);

    void delete(Category category);

}
