package com.marketing.web.services.product;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.repositories.ProductSpecifyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductSpecifyServiceImpl implements ProductSpecifyService {


    private Logger logger = LoggerFactory.getLogger(ProductSpecifyServiceImpl.class);

    @Autowired
    private ProductSpecifyRepository productSpecifyRepository;

    @Override
    public Page<ProductSpecify> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ProductSpecify> resultPage = productSpecifyRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public List<ProductSpecify> findAllWithoutPagination() {
       return productSpecifyRepository.findAll();
    }

    @Override
    public Page<ProductSpecify> findAllByUser(User user, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ProductSpecify> resultPage = productSpecifyRepository.findAllByUser(user, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public List<ProductSpecify> findAllByUserWithoutPagination(User user) {
       return productSpecifyRepository.findAllByUser(user);
    }

    @Override
    public Page<ProductSpecify> findAllByProduct(Product product, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ProductSpecify> resultPage = productSpecifyRepository.findAllByProduct(product, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public List<ProductSpecify> findAllByProductWithoutPagination(Product product){
        return productSpecifyRepository.findAllByProduct(product);
    }

    @Override
    public Page<ProductSpecify> findAllByProductAndStates(Product product, List<State> states, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ProductSpecify> resultPage = productSpecifyRepository.findAllByProductAndStatesIn(product,states, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public List<ProductSpecify> findAllByProductAndStatesLimit(Product product, List<State> states, int limit) {
        PageRequest pageRequest = PageRequest.of(0,2, Sort.by(Sort.Direction.ASC,"totalPrice"));
        Page<ProductSpecify> resultPage = productSpecifyRepository.findAllByProductAndStatesIn(product,states, pageRequest);
        return resultPage.getContent();
    }

    @Override
    public Page<ProductSpecify> findAllByProductAndUser(Product product, User user, int pageNumber, String sortBy, String sortType){
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ProductSpecify> resultPage = productSpecifyRepository.findAllByProductAndUser(product, user, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public ProductSpecify findById(Long id) {
        return productSpecifyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product.specify",id.toString()));
    }

    @Override
    public ProductSpecify findByUUID(String uuid) {
        return productSpecifyRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product.specify", uuid));
    }

    @Override
    public ProductSpecify findByUUIDAndUser(String uuid,User user) {
        return productSpecifyRepository.findByUuidAndUser_Id(UUID.fromString(uuid),user.getId()).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product.specify", uuid));
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
        productSpecify.setPromotion(updatedProductSpecify.getPromotion());
        return productSpecifyRepository.save(productSpecify);
    }

    @Override
    public List<ProductSpecify> updateAll(List<ProductSpecify> productSpecifyList) {
        return productSpecifyRepository.saveAll(productSpecifyList);
    }

    @Override
    public void delete(ProductSpecify productSpecify) {
        productSpecifyRepository.delete(productSpecify);
    }

    @Override
    public List<State> allowedStates(User user, List<State> states){
        boolean isAllowed = user.getActiveStates().containsAll(states);
        if (isAllowed){
            return states;
        }
        throw new BadRequestException("You can only add your active states for product");
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
       return PageRequest.of(pageNumber-1,20, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}

