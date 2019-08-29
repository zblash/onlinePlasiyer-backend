package com.marketing.web.services.product;

import com.marketing.web.dtos.product.ProductSpecifyDTO;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.User;
import java.util.List;

public interface IProductSpecifyService {

    List<ProductSpecify> findAll();

    ProductSpecify findById(Long id);

    ProductSpecify create(ProductSpecifyDTO productSpecifyDTO, Product product, User user);

    ProductSpecify update(ProductSpecify productSpecify);

    void delete(ProductSpecify productSpecify);
}
