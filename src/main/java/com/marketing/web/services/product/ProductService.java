package com.marketing.web.services.product;

import com.marketing.web.dtos.product.WritableProduct;
import com.marketing.web.models.Category;
import com.marketing.web.models.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAllByStatus(boolean status);

    List<Product> findAllByCategory(Category category);

    List<Product> findAllByCategoryAndStatus(Category category, boolean status);

    Product findByBarcode(String barcode);

    List<Product> findAll();

    Product findById(Long id);

    Product findByUUID(String uuid);

    Product create(Product product);

    Product update(String uuid,Product updatedProduct);

    void delete(Product product);

    List<Product> filterByState(List<Product> products, String userState);
}
