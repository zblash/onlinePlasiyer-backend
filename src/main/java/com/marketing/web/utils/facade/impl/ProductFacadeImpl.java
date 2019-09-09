package com.marketing.web.utils.facade.impl;

import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.*;
import com.marketing.web.pubsub.ProductProducer;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.services.product.ProductService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.user.StateService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.facade.ProductFacade;
import com.marketing.web.utils.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service(value = "productFacade")
public class ProductFacadeImpl implements ProductFacade {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    @Autowired
    private StateService stateService;

    @Override
    public ReadableProductSpecify createProductSpecify(WritableProductSpecify writableProductSpecify, User user){
        Product product = productService.findByBarcode(writableProductSpecify.getBarcode());
        if (product == null){
            throw new ResourceNotFoundException("Product not found with barcode: "+writableProductSpecify.getBarcode());
        }
        ProductSpecify productSpecify = ProductMapper.INSTANCE.writableProductSpecifyToProductSpecify(writableProductSpecify);

        List<State> states = stateService.findAllByUuids(writableProductSpecify.getStateList());


        productSpecify.setProduct(product);
        productSpecify.setUser(user);
        productSpecify.setStates(productSpecifyService.allowedStates(user,states));
        return ProductMapper.INSTANCE.productSpecifyToReadableProductSpecify(productSpecifyService.create(productSpecify));
    }

    @Override
    public ReadableProductSpecify updateProductSpecify(String uuid, WritableProductSpecify writableProductSpecify, User user) {
        Product product = productService.findByBarcode(writableProductSpecify.getBarcode());
        if (product == null){
            throw new ResourceNotFoundException("Product not found with barcode: "+writableProductSpecify.getBarcode());
        }
        ProductSpecify productSpecify = ProductMapper.INSTANCE.writableProductSpecifyToProductSpecify(writableProductSpecify);


        List<State> states = stateService.findAllByUuids(writableProductSpecify.getStateList());


        productSpecify.setStates(productSpecifyService.allowedStates(user,states));
        productSpecify.setProduct(product);
        return ProductMapper.INSTANCE.productSpecifyToReadableProductSpecify(productSpecifyService.update(uuid,productSpecify));
    }
}
