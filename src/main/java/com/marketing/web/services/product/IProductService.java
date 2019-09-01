package com.marketing.web.services.product;

import com.marketing.web.dtos.product.ProductDTO;
import com.marketing.web.models.Product;

import java.util.List;

public interface IProductService {

    List<Product> findAllByStatus(boolean status);

    List<Product> findByCategory(Long categoryId);

    Product findByBarcode(String barcode);

    List<Product> findAll();

    Product findById(Long id);

    Product create(ProductDTO productDTO);

    Product update(Product product,Product updatedProduct);

    void delete(Product product);

    List<Product> filterByState(List<Product> products, String userState);
}