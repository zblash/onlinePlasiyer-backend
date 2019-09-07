package com.marketing.web.repositories;

import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByCategoryIn(List<Category> categories);

    List<Product> findAllByStatus(boolean status);

    Optional<Product> findByUuid(UUID uuid);
}
