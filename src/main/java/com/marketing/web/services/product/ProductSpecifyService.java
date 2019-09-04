package com.marketing.web.services.product;

import com.marketing.web.dtos.product.ProductSpecifyDTO;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.City;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.ProductSpecifyRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.utils.mappers.ProductSpecifyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    public ProductSpecify create(ProductSpecifyDTO productSpecifyDTO, Product product, User user) {
        List<State> states = new CopyOnWriteArrayList<>();
        if (!productSpecifyDTO.getStateList().isEmpty() && productSpecifyDTO.getStateList() != null){
                states.addAll(stateRepository.findAllByTitleIn(productSpecifyDTO.getStateList()));
        }else if(!productSpecifyDTO.getCity().isEmpty()){
           City city = cityRepository.findByTitle(productSpecifyDTO.getCity().toUpperCase()).orElseThrow(() -> new ResourceNotFoundException("City not found with name:" + productSpecifyDTO.getCity().toUpperCase()));
           states.addAll(stateRepository.findAllByCity(city));
        }


        ProductSpecify productSpecify = ProductSpecifyMapper.INSTANCE.dtoToProductSpecify(productSpecifyDTO);
        productSpecify.setProduct(product);
        productSpecify.setUser(user);
        productSpecify.setStates(allowedStates(user,states));
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

    private List<State> allowedStates(User user, List<State> states){
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

