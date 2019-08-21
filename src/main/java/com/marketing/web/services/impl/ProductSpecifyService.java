package com.marketing.web.services.impl;

import com.marketing.web.dtos.ProductDTO;
import com.marketing.web.dtos.ProductSpecifyDTO;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.User;
import com.marketing.web.repositories.ProductSpecifyRepository;
import com.marketing.web.services.IProductSpecifyService;
import com.marketing.web.utils.mappers.ProductMapper;
import com.marketing.web.utils.mappers.ProductSpecifyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSpecifyService implements IProductSpecifyService {


    private Logger logger = LoggerFactory.getLogger(ProductSpecifyService.class);

    @Autowired
    private ProductSpecifyRepository productSpecifyRepository;

    @Override
    public List<ProductSpecify> findAll() {
        return productSpecifyRepository.findAll();
    }

    @Override
    public ProductSpecify findById(Long id) {
        return productSpecifyRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public ProductSpecify create(ProductSpecifyDTO productSpecifyDTO, Product product, User user) {
        ProductSpecify productSpecify = ProductSpecifyMapper.INSTANCE.dtoToProductSpecify(productSpecifyDTO);
        productSpecify.setProduct(product);
        productSpecify.setUser(user);
        productSpecify.setUnitType(productSpecifyDTO.getUnitType());

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
