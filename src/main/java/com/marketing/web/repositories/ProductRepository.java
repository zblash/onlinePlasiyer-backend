package com.marketing.web.repositories;

import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findAll(Pageable pageable);

    Page<Product> findAllByCategoryIn(List<Category> categories, Pageable pageable);

    List<Product> findAllByCategoryIn(List<Category> categories);

    Page<Product> findAllByStatus(boolean status, Pageable pageable);

    Optional<Product> findByName(String name);

    Optional<Product> findByUuid(UUID uuid);

    Page<Product> findAllByCategoryInAndStatus(List<Category> categories, boolean status, Pageable pageable);

    List<Product> findAllByNameLikeIgnoreCase(String name);
}
