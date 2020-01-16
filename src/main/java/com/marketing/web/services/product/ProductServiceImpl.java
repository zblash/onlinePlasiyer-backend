package com.marketing.web.services.product;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.User;
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

    @Autowired
    private ProductRepository productRepository;

    private Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public List<Product> simpleFilterByName(String name) {
       return productRepository.findAllByNameLikeIgnoreCase("%"+name+"%");
    }

    @Override
    public List<Product> findAllByUserWithoutPagination(User user) {
        return productRepository.findAllByUsers_Id(user.getId());
    }

    @Override
    public Page<Product> findAllByUser(User user, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByUsers_Id(user.getId(), pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Page<Product> findAllByStatus(boolean status, int pageNumber, String sortBy, String sortType){
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByStatus(status,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Page<Product> findAllByCategory(Category category, int pageNumber, String sortBy, String sortType){
        List<Category> categories = category.collectLeafChildren();
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByCategoryIn(categories,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Page<Product> findAllByCategoryAndStatus(Category category, boolean status, int pageNumber, String sortBy, String sortType){
        List<Category> categories = category.collectLeafChildren();
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByCategoryInAndStatus(categories, status,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;

    }

    @Override
    public Page<Product> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public List<Product> findAllWithoutPagination(String sortBy, String sortType) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy);
        return productRepository.findAll(sort);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: "+id));
    }

    @Override
    public Product findByName(String name) {
        return productRepository.findByName(name).orElse(null);
    }

    @Override
    public Product findByUUID(String uuid) {
        return productRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: "+uuid));
    }

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product update(String uuid,Product updatedProduct) {
        Product product = findByUUID(uuid);
        product.setBarcodes(updatedProduct.getBarcodes());
        product.setName(updatedProduct.getName());
        if (updatedProduct.getPhotoUrl() != null && !updatedProduct.getPhotoUrl().isEmpty()) {
            product.setPhotoUrl(updatedProduct.getPhotoUrl());
        }
        product.setUsers(updatedProduct.getUsers());
        product.setCategory(updatedProduct.getCategory());
        return productRepository.save(product);
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }

    @Override
    public Page<Product> findAllByCategoryAndUser(Category category, User user, Integer pageNumber, String sortBy, String sortType) {
        List<Category> categories = category.collectLeafChildren();
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByCategoryInAndUsers_Id(categories, user.getId(), pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Page<Product> findAllByCategoryAndUserAndStatus(Category category, User user, boolean status, Integer pageNumber, String sortBy, String sortType) {
        List<Category> categories = category.collectLeafChildren();
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Product> resultPage = productRepository.findAllByCategoryInAndUsers_IdAndStatus(categories, user.getId(), status, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
