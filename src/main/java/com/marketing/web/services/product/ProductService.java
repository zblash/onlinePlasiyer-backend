package com.marketing.web.services.product;

import com.marketing.web.dtos.product.WritableProduct;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.repositories.ProductRepository;
import com.marketing.web.services.category.CategoryService;
import com.marketing.web.utils.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Override
    public List<Product> findAllByStatus(boolean status){
        return productRepository.findAllByStatus(status);
    }

    @Override
    public List<Product> findAllByCategory(Category category){
        List<Category> categories = category.collectLeafChildren();
        return productRepository.findByCategoryIn(categories);

    }

    @Override
    public List<Product> findAllByCategoryAndStatus(Category category, boolean status){
        List<Category> categories = category.collectLeafChildren();
        return productRepository.findByCategoryInAndStatus(categories, status);

    }

    @Override
    public Product findByBarcode(String barcode){
        return productRepository.findByBarcode(barcode).orElse(null);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: "+id));
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
        product.setBarcode(updatedProduct.getBarcode());
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
