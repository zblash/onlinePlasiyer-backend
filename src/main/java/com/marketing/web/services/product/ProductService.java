package com.marketing.web.services.product;

import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.BitSet;
import java.util.List;

public interface ProductService {

    List<Product> simpleFilterByName(String name);

    List<Product> findAllByUserWithoutPagination(User user);

    Page<Product> findAllByUser(User user, int pageNumber, String sortBy, String sortType);

    Page<Product> findAllByStatus(boolean status, int pageNumber, String sortBy, String sortType);

    Page<Product> findAllByCategory(Category category, int pageNumber, String sortBy, String sortType);

    Page<Product> findAllByCategoryAndStatus(Category category, boolean status, int pageNumber, String sortBy, String sortType);

    Page<Product> findAll(int pageNumber, String sortBy, String sortType);

    List<Product> findAllWithoutPagination(String sortBy, String sortType);

    Product findById(Long id);

    Product findByName(String name);

    Product findByUUID(String uuid);

    Product findByUUIDAndUser(String uuid, User user);

    Product create(Product product);

    Product update(String uuid,Product updatedProduct);

    void delete(Product product);

    Page<Product> findAllByCategoryAndUser(Category category, User user, Integer pageNumber, String sortBy, String sortType);

    Page<Product> findAllByCategoryAndUserAndStatus(Category category, User user, boolean status, Integer pageNumber, String sortBy, String sortType);

}
