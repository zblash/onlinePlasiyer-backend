package com.marketing.web.services.product;

import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.City;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.ProductSpecifyRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.utils.mappers.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ProductSpecifyService implements IProductSpecifyService {


    private Logger logger = LoggerFactory.getLogger(ProductSpecifyService.class);

    @Autowired
    private ProductSpecifyRepository productSpecifyRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;
    @Override
    public List<ProductSpecify> findAll() {
        return productSpecifyRepository.findAll();
    }

    @Override
    public ProductSpecify findById(Long id) {
        return productSpecifyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ProductSpecify not found with id:" + id));
    }

    @Override
    public ProductSpecify findByUUID(String uuid) {
        return productSpecifyRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("ProductSpecify not found with id:" + uuid));
    }

    @Override
    public ProductSpecify findByUUIDAndUser(String uuid,User user) {
        return productSpecifyRepository.findByUuidAndUser_Id(UUID.fromString(uuid),user.getId()).orElseThrow(() -> new ResourceNotFoundException("ProductSpecify not found with id:" + uuid));
    }

    @Override
    public ProductSpecify create(ProductSpecify productSpecify) {
        return productSpecifyRepository.save(productSpecify);
    }

    @Override
    public ProductSpecify update(String uuid,ProductSpecify updatedProductSpecify) {
        ProductSpecify productSpecify = findByUUID(uuid);
        productSpecify.setUnitType(updatedProductSpecify.getUnitType());
        productSpecify.setContents(updatedProductSpecify.getContents());
        productSpecify.setQuantity(updatedProductSpecify.getQuantity());
        productSpecify.setRecommendedRetailPrice(updatedProductSpecify.getRecommendedRetailPrice());
        productSpecify.setTotalPrice(updatedProductSpecify.getTotalPrice());
        productSpecify.setUnitPrice(updatedProductSpecify.getUnitPrice());
        productSpecify.setProduct(updatedProductSpecify.getProduct());
        productSpecify.setStates(updatedProductSpecify.getStates());
        productSpecify.setProduct(updatedProductSpecify.getProduct());
        return productSpecifyRepository.save(productSpecify);
    }

    @Override
    public void delete(ProductSpecify productSpecify) {
        productSpecifyRepository.delete(productSpecify);
    }

    public List<State> allowedStates(User user, List<State> states){
        boolean isAllowed = user.getActiveStates().containsAll(states);
        if (isAllowed){
            return states;
        }
        List<State> allowedStates = new ArrayList<>();
        for (State state : states){
            if (user.getActiveStates().contains(state)){
                allowedStates.add(state);
            }
        }
        return allowedStates.isEmpty() ? user.getActiveStates() : allowedStates;
    }
}

