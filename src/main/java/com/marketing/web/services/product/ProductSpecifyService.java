package com.marketing.web.services.product;

import com.marketing.web.models.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductSpecifyService {

    Page<ProductSpecify> findAll(int pageNumber, String sortBy, String sortType);

    Page<ProductSpecify> findAllByProduct(Product product, int pageNumber, String sortBy, String sortType);

    Page<ProductSpecify> findAllByProductAndMerchant(Product product, Merchant merchant, int pageNumber, String sortBy, String sortType);

    Page<ProductSpecify> findAllByMerchant(Merchant merchant, int pageNumber, String sortBy, String sortType);

    List<ProductSpecify> findAllByMerchant(Merchant merchant);

    Page<ProductSpecify> findAllByProductAndStates(Product product, List<State> states, int pageNumber, String sortBy, String sortType);

    ProductSpecify findById(String id);

    ProductSpecify findByIdAndMerchant(String id, Merchant merchant);

    ProductSpecify create(ProductSpecify productSpecify);

    ProductSpecify update(String id, ProductSpecify updatedProductSpecify);

    List<ProductSpecify> updateAll(List<ProductSpecify> productSpecifyList);

    void delete(ProductSpecify productSpecify);

    List<State> allowedStates(Merchant merchant, List<State> states);

}
