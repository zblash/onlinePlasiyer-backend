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

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAll(Pageable pageable);

    Page<Product> findAllByCategoryIn(List<Category> categories, Pageable pageable);

    List<Product> findAllByCategoryIn(List<Category> categories);

    Page<Product> findAllByStatus(boolean status, Pageable pageable);

    Optional<Product> findByName(String name);

    Page<Product> findAllByCategoryInAndStatus(List<Category> categories, boolean status, Pageable pageable);

    List<Product> findAllByNameLikeIgnoreCase(String name);

    Page<Product> findAllByCategoryInAndMerchants_Id(List<Category> categories, UUID id, Pageable pageable);

    Page<Product> findAllByCategoryInAndMerchants_IdAndStatus(List<Category> categories, UUID id, boolean status, Pageable pageable);

    List<Product> findAllByMerchants_Id(UUID id);

    Page<Product> findAllByMerchants_Id(UUID id, Pageable pageable);

    Optional<Product> findByIdAndMerchants_Id(UUID id, UUID merchantId);

    List<Product> findAllByCategoryId(UUID categoryId);
}
