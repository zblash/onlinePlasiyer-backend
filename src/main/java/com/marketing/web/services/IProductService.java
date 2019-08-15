package com.marketing.web.services;

import com.marketing.web.dtos.ProductDTO;
import com.marketing.web.models.Product;

import java.util.List;

public interface IProductService {

    List<Product> findByCategory(Long categoryId);

    Product findByBarcode(String barcode);

    List<Product> findAll();

    Product findById(Long id);

    Product create(Product product);

    Product create(ProductDTO productDTO);

    Product update(Product product,Product updatedProduct);

    void delete(Product product);

}
