package com.marketing.web.services.product;

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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductSpecifyServiceImpl implements ProductSpecifyService {


    private Logger logger = LoggerFactory.getLogger(ProductSpecifyServiceImpl.class);

    @Autowired
    private ProductSpecifyRepository productSpecifyRepository;

    @Override
    public List<ProductSpecify> findAll() {
        return productSpecifyRepository.findAll();
    }

    @Override
    public List<ProductSpecify> findAllByUser(User user) {
        return productSpecifyRepository.findAllByUserOrderByIdDesc(user);
    }

    @Override
    public List<ProductSpecify> findAllByProduct(Product product) {
        return productSpecifyRepository.findAllByProductOrderByIdDesc(product);
    }

    @Override
    public List<ProductSpecify> findAllByProductAndStates(Product product, List<State> states) {
        return productSpecifyRepository.findAllByProductAndStatesInOrderByIdDesc(product,states);
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

    @Override
    public List<State> allowedStates(User user, List<State> states){
        for (State state : user.getActiveStates()){
            logger.info("User state title " +state.getUuid());
            logger.info("User state title " +state.getTitle());
            logger.info("User state title " +state.getCode());
            logger.info("User state title " +state.getId());
        }

        for (State state : states){
            logger.info("request state title " +state.getUuid());
            logger.info("request state title " +state.getTitle());
            logger.info("request state title " +state.getCode());
            logger.info("request state title " +state.getId());
        }

        boolean isAllowed = user.getActiveStates().containsAll(states);
        logger.info("isAllowed "+isAllowed);
        if (isAllowed){
            return states;
        }
        throw new BadRequestException("You can only add your active states for product");
    }
}

