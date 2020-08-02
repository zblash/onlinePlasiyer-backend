package com.marketing.web.repositories;

import com.marketing.web.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByOrderByIdDesc();

    List<Category> findAllBySubCategoryOrderByIdDesc(boolean subCategory);

}
