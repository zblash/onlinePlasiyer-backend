package com.marketing.web.services.product;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.repositories.ProductRepository;
import com.marketing.web.services.category.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Override
    public Page<Product> findAllByStatus(boolean status, int pageNumber){
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15);
        Page<Product> resultPage = productRepository.findAllByStatusOrderByIdDesc(status,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Page<Product> findAllByCategory(Category category, int pageNumber){
        List<Category> categories = category.collectLeafChildren();
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15);
        Page<Product> resultPage = productRepository.findAllByCategoryInOrderByIdDesc(categories,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Page<Product> findAllByCategoryAndStatus(Category category, boolean status, int pageNumber){
        List<Category> categories = category.collectLeafChildren();
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15);
        Page<Product> resultPage = productRepository.findAllByCategoryInAndStatusOrderByIdDesc(categories, status,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;

    }

    @Override
    public Page<Product> findAll(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15);
        Page<Product> resultPage = productRepository.findAllByOrderByIdDesc(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
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
        product.setCategory(updatedProduct.getCategory());
        return productRepository.save(product);
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }

    @Override
    public List<Product> filterByState(List<Product> products, String userState) {
        for (Product product : products){
            Set<ProductSpecify> filteredProductSpecifies = new HashSet<>();
            for (ProductSpecify productSpecify : product.getProductSpecifies()){
                long stateCount = productSpecify.getStates().stream()
                        .filter(state -> state.getTitle().equals(userState)).count();
                if (stateCount > 0){
                    filteredProductSpecifies.add(productSpecify);
                }
            }
            product.setProductSpecifies(filteredProductSpecifies);
        }
        return products;
    }
}
