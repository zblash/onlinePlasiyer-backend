package com.marketing.web.repositories;

import com.marketing.web.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<List<Product>> findByCategoryId(Long categoryId);
}
