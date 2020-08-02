package com.marketing.web.services.product;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.*;
import com.marketing.web.repositories.ProductRepository;
import com.marketing.web.services.category.CategoryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> simpleFilterByName(String name) {
       return productRepository.findAllByNameLikeIgnoreCase("%"+name+"%");
    }

    @Override
    public List<Product> findAllByMerchantWithoutPagination(Merchant merchant) {
        return productRepository.findAllByMerchants_Id(merchant.getId());
    }

    @Override
    public List<Product> findAllByCategoryId(String categoryId) {
       return productRepository.findAllByCategoryId(UUID.fromString(categoryId));
    }

    @Override
    public Page<Product> findAllByMerchant(Merchant merchant, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByMerchants_Id(merchant.getId(), pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<Product> findAllByStatus(boolean status, int pageNumber, String sortBy, String sortType){
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByStatus(status,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<Product> findAllByCategory(Category category, int pageNumber, String sortBy, String sortType){
        List<Category> categories = category.collectLeafChildren();
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByCategoryIn(categories,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<Product> findAllByCategoryAndStatus(Category category, boolean status, int pageNumber, String sortBy, String sortType){
        List<Category> categories = category.collectLeafChildren();
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByCategoryInAndStatus(categories, status,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;

    }

    @Override
    public Page<Product> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public List<Product> findAllWithoutPagination(String sortBy, String sortType) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy);
        return productRepository.findAll(sort);
    }

    @Override
    public Product findById(String id) {
        return productRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product",id.toString()));
    }

    @Override
    public Product findByName(String name) {
        return productRepository.findByName(name).orElse(null);
    }

    @Override
    public Product findByUUIDAndMerchant(String uuid, Merchant merchant) {
        return productRepository.findByIdAndMerchants_Id(UUID.fromString(uuid), merchant.getId()).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product",uuid));
    }

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        return productRepository.saveAll(products);
    }

    @Override
    public Product update(String id,Product updatedProduct) {
        Product product = findById(id);
        product.setBarcodes(updatedProduct.getBarcodes());
        product.setName(updatedProduct.getName());
        if (updatedProduct.getPhotoUrl() != null && !updatedProduct.getPhotoUrl().isEmpty()) {
            product.setPhotoUrl(updatedProduct.getPhotoUrl());
        }
        product.setMerchants(updatedProduct.getMerchants());
        product.setCategory(updatedProduct.getCategory());
        return productRepository.save(product);
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }

    @Override
    public Page<Product> findAllByCategoryAndMerchant(Category category, Merchant merchant, Integer pageNumber, String sortBy, String sortType) {
        List<Category> categories = category.collectLeafChildren();
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByCategoryInAndMerchants_Id(categories, merchant.getId(), pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<Product> findAllByCategoryAndMerchantAndStatus(Category category, Merchant merchant, boolean status, Integer pageNumber, String sortBy, String sortType) {
        List<Category> categories = category.collectLeafChildren();
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByCategoryInAndMerchants_IdAndStatus(categories, merchant.getId(), status, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", Integer.toString(pageNumber));
        }
        return resultPage;
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
