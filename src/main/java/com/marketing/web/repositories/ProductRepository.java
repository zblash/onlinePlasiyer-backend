package com.marketing.web.repositories;

import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.models.User;
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

    Page<Product> findAllByCategoryInAndUsers_Id(List<Category> categories, Long id, Pageable pageable);

    Page<Product> findAllByCategoryInAndUsers_IdAndStatus(List<Category> categories, Long id, boolean status, Pageable pageable);

    List<Product> findAllByUsers_Id(Long id);

    Page<Product> findAllByUsers_Id(Long id, Pageable pageable);

    Optional<Product> findByUuidAndUsers_Id(UUID uuid, Long id);

    List<Product> findAllByCategoryId(Long categoryId);
}
