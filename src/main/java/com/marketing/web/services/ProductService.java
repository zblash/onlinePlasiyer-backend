package com.marketing.web.services;

import com.marketing.web.models.Product;
import com.marketing.web.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements BaseService<Product> {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findByCategory(Long categoryId){
        return productRepository.findByCategoryId(categoryId)
                .orElseThrow(RuntimeException::new);
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

    @Override
    public Product update(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }
}
