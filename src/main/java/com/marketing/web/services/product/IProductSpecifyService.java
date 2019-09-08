package com.marketing.web.services.product;

import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.User;
import java.util.List;

public interface IProductSpecifyService {

    List<ProductSpecify> findAll();

    ProductSpecify findById(Long id);

    ProductSpecify findByUUID(String uuid);

    ProductSpecify findByUUIDAndUser(String uuid,User user);

    ProductSpecify create(ProductSpecify productSpecify);

    ProductSpecify update(String uuid,ProductSpecify updatedProductSpecify);

    void delete(ProductSpecify productSpecify);
}
