package com.marketing.web.services.product;

import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductSpecifyService {

    Page<ProductSpecify> findAll(int pageNumber, String sortBy, String sortType);

    List<ProductSpecify> findAllWithoutPagination();

    Page<ProductSpecify> findAllByUser(User user, int pageNumber, String sortBy, String sortType);

    List<ProductSpecify> findAllByUserWithoutPagination(User user);

    Page<ProductSpecify> findAllByProduct(Product product, int pageNumber, String sortBy, String sortType);

    List<ProductSpecify> findAllByProductWithoutPagination(Product product);

    Page<ProductSpecify> findAllByProductAndStates(Product product, List<State> states, int pageNumber, String sortBy, String sortType);

    List<ProductSpecify> findAllByProductAndStatesLimit(Product product, List<State> states, int limit);

    Page<ProductSpecify> findAllByProductAndUser(Product product, User user, int pageNumber, String sortBy, String sortType);

    ProductSpecify findById(Long id);

    ProductSpecify findByUUID(String uuid);

    ProductSpecify findByUUIDAndUser(String uuid,User user);

    ProductSpecify create(ProductSpecify productSpecify);

    ProductSpecify update(String uuid,ProductSpecify updatedProductSpecify);

    List<ProductSpecify> updateAll(List<ProductSpecify> productSpecifyList);

    void delete(ProductSpecify productSpecify);

    List<State> allowedStates(User user, List<State> states);

}
