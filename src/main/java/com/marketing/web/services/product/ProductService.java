package com.marketing.web.services.product;

import com.marketing.web.models.Category;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.Product;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.BitSet;
import java.util.List;

public interface ProductService {

    List<Product> simpleFilterByName(String name);

    List<Product> findAllByMerchantWithoutPagination(Merchant merchant);

    List<Product> findAllByCategoryId(String categoryId);

    Page<Product> findAllByMerchant(Merchant merchant, int pageNumber, String sortBy, String sortType);

    Page<Product> findAllByStatus(boolean status, int pageNumber, String sortBy, String sortType);

    Page<Product> findAllByCategory(Category category, int pageNumber, String sortBy, String sortType);

    Page<Product> findAllByCategoryAndStatus(Category category, boolean status, int pageNumber, String sortBy, String sortType);

    Page<Product> findAll(int pageNumber, String sortBy, String sortType);

    List<Product> findAllWithoutPagination(String sortBy, String sortType);

    Product findById(String id);

    Product findByName(String name);

    Product findByUUIDAndMerchant(String uuid, Merchant merchant);

    Product create(Product product);

    List<Product> saveAll(List<Product> products);

    Product update(String id,Product updatedProduct);

    void delete(Product product);

    Page<Product> findAllByCategoryAndMerchant(Category category, Merchant merchant, Integer pageNumber, String sortBy, String sortType);

    Page<Product> findAllByCategoryAndMerchantAndStatus(Category category, Merchant merchant, boolean status, Integer pageNumber, String sortBy, String sortType);

}
