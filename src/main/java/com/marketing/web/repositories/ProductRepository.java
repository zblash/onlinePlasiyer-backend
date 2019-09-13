package com.marketing.web.repositories;

import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findAllByOrderByIdDesc();

    Optional<Product> findByBarcode(String barcode);

    List<Product> findAllByCategoryInOrderByIdDesc(List<Category> categories);

    List<Product> findAllByStatusOrderByIdDesc(boolean status);

    Optional<Product> findByUuid(UUID uuid);

    List<Product> findAllByCategoryInAndStatusOrderByIdDesc(List<Category> categories, boolean status);
}
