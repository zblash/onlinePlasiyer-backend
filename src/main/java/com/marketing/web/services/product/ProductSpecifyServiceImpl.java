package com.marketing.web.services.product;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.*;
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

    private final ProductSpecifyRepository productSpecifyRepository;

    public ProductSpecifyServiceImpl(ProductSpecifyRepository productSpecifyRepository) {
        this.productSpecifyRepository = productSpecifyRepository;
    }

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
    public Page<ProductSpecify> findAllByProduct(Product product, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ProductSpecify> resultPage = productSpecifyRepository.findAllByProduct(product, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<ProductSpecify> findAllByProductAndMerchant(Product product, Merchant merchant, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ProductSpecify> resultPage = productSpecifyRepository.findAllByProductAndMerchant(product, merchant, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<ProductSpecify> findAllByMerchant(Merchant merchant, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<ProductSpecify> resultPage = productSpecifyRepository.findAllByMerchant(merchant, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public List<ProductSpecify> findAllByMerchant(Merchant merchant) {
        return productSpecifyRepository.findAllByMerchant(merchant);
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
    public ProductSpecify findById(String id) {
        return productSpecifyRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product.specify",id));
    }

    @Override
    public ProductSpecify findByIdAndMerchant(String id, Merchant merchant) {
        return productSpecifyRepository.findByIdAndMerchant(UUID.fromString(id), merchant).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"product.specify", id));
    }

    @Override
    public ProductSpecify create(ProductSpecify productSpecify) {
        return productSpecifyRepository.save(productSpecify);
    }

    @Override
    public ProductSpecify update(String id,ProductSpecify updatedProductSpecify) {
        ProductSpecify productSpecify = findById(id);
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
    public List<State> allowedStates(Merchant merchant, List<State> states){
        boolean isAllowed = merchant.getActiveStates().containsAll(states);
        if (isAllowed){
            return states;
        }
        throw new BadRequestException("You can only add your active states for product");
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
       return PageRequest.of(pageNumber-1,20, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}

