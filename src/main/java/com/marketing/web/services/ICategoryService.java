package com.marketing.web.services;

import com.marketing.web.models.Category;
import java.util.List;
public interface ICategoryService {

    List<Category> findAll();

    Category findById(Long id);

    Category create(Category category);

    Category update(Category category);

    void delete(Category category);
}
