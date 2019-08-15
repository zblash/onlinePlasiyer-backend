package com.marketing.web.services.impl;

import com.marketing.web.dtos.ProductDTO;
import com.marketing.web.models.Product;
import com.marketing.web.repositories.ProductRepository;
import com.marketing.web.services.IProductService;
import com.marketing.web.utils.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    public List<Product> findByCategory(Long categoryId){
        return productRepository.findByCategoryId(categoryId)
                .orElseThrow(RuntimeException::new);
    }

    public Product findByBarcode(String barcode){
        return productRepository.findByBarcode(barcode).orElse(null);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product create(ProductDTO productDTO) {
        Product product = ProductMapper.INSTANCE.ProductDTOtoProduct(productDTO);
        return productRepository.save(product);
    }

    @Override
    public Product update(Product product,Product updatedProduct) {
        product.setBarcode(updatedProduct.getBarcode());
        product.setName(updatedProduct.getName());
        product.setPhotoUrl(updatedProduct.getPhotoUrl());
        product.setCategory(updatedProduct.getCategory());
        return productRepository.save(product);
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }
}
