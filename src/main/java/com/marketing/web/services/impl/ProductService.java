package com.marketing.web.services.impl;

import com.marketing.web.dtos.ProductDTO;
import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.repositories.ProductRepository;
import com.marketing.web.services.IProductService;
import com.marketing.web.utils.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Override
    public List<Product> findByCategory(Long categoryId){
        Category category = categoryService.findById(categoryId);
        List<Category> categories = category.collectLeafChildren();
        return productRepository.findByCategoryIn(categories);

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
    public Product create(ProductDTO productDTO, boolean status) {
        Product product = ProductMapper.INSTANCE.ProductDTOtoProduct(productDTO);
        product.setCategory(categoryService.findById(productDTO.getCategoryId()));
        product.setStatus(status);
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
