package com.marketing.web.services.product;

import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Page<Product> findAllByStatus(boolean status, int pageNumber, String sortBy, String sortType);

    Page<Product> findAllByCategory(Category category, int pageNumber, String sortBy, String sortType);

    Page<Product> findAllByCategoryAndStatus(Category category, boolean status, int pageNumber, String sortBy, String sortType);

    Page<Product> findAll(int pageNumber, String sortBy, String sortType);

    Product findById(Long id);

    Product findByName(String name);

    Product findByUUID(String uuid);

    Product create(Product product);

    Product update(String uuid,Product updatedProduct);

    void delete(Product product);

    List<Product> filterByState(List<Product> products, String userState);
}
