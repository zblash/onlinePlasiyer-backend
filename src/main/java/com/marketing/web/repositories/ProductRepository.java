package com.marketing.web.repositories;

import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findByBarcode(String barcode);
    List<Product> findByCategoryIn(List<Category> categories);
}
