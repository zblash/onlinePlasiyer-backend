package com.marketing.web.services;

import com.marketing.web.dtos.ProductDTO;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.User;
import com.marketing.web.repositories.ProductSpecifyRepository;
import com.marketing.web.utils.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSpecifyService implements BaseService<ProductSpecify> {

    @Autowired
    private ProductSpecifyRepository productSpecifyRepository;

    @Override
    public List<ProductSpecify> findAll() {
        return null;
    }

    @Override
    public ProductSpecify findById(Long id) {
        return null;
    }

    @Override
    public ProductSpecify create(ProductSpecify productSpecify) {
        return null;
    }

    public ProductSpecify create(ProductDTO productDTO, Product product, User user) {
        ProductSpecify productSpecify = ProductMapper.INSTANCE.ProductDTOtoProductSpecify(productDTO);
        productSpecify.setProduct(product);
        productSpecify.setUser(user);
        return productSpecifyRepository.save(productSpecify);
    }

    @Override
    public ProductSpecify update(ProductSpecify productSpecify) {
        return null;
    }

    @Override
    public void delete(ProductSpecify productSpecify) {

    }
}
