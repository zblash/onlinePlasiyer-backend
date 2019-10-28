package com.marketing.web.services.product;

import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductSpecifyService {

    Page<ProductSpecify> findAll(int pageNumber);

    Page<ProductSpecify> findAllByUser(User user, int pageNumber);

    Page<ProductSpecify> findAllByProduct(Product product, int pageNumber);

    Page<ProductSpecify> findAllByProductAndStates(Product product, List<State> states, int pageNumber);

    ProductSpecify findById(Long id);

    ProductSpecify findByUUID(String uuid);

    ProductSpecify findByUUIDAndUser(String uuid,User user);

    ProductSpecify create(ProductSpecify productSpecify);

    ProductSpecify update(String uuid,ProductSpecify updatedProductSpecify);

    void delete(ProductSpecify productSpecify);

    List<State> allowedStates(User user, List<State> states);

}
