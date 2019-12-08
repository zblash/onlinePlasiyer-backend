package com.marketing.web.utils.facade.impl;

import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.*;
import com.marketing.web.services.product.*;
import com.marketing.web.services.user.StateService;
import com.marketing.web.services.user.StateServiceImpl;
import com.marketing.web.services.user.UserService;
import com.marketing.web.services.user.UserServiceImpl;
import com.marketing.web.utils.facade.ProductFacade;
import com.marketing.web.utils.mappers.ProductMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "productFacade")
public class ProductFacadeImpl implements ProductFacade {

    @Autowired
    private UserService userService;

    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    @Autowired
    private StateService stateService;

    @Override
    public ReadableProductSpecify createProductSpecify(WritableProductSpecify writableProductSpecify, User user){
        Barcode barcode = barcodeService.findByBarcodeNo(writableProductSpecify.getBarcode());
        if (barcode == null || barcode.getProduct() == null) {
            throw new ResourceNotFoundException("Product not found with barcode: "+writableProductSpecify.getBarcode());
        }

        ProductSpecify productSpecify = ProductMapper.writableProductSpecifyToProductSpecify(writableProductSpecify);

        List<State> states = stateService.findAllByUuids(writableProductSpecify.getStateList());


        productSpecify.setProduct(barcode.getProduct());
        productSpecify.setUser(user);
        productSpecify.setStates(productSpecifyService.allowedStates(user,states));
        return ProductMapper.productSpecifyToReadableProductSpecify(productSpecifyService.create(productSpecify));
    }

    @Override
    public ReadableProductSpecify updateProductSpecify(String uuid, WritableProductSpecify writableProductSpecify, User user) {
        Barcode barcode = barcodeService.findByBarcodeNo(writableProductSpecify.getBarcode());
        if (barcode == null || barcode.getProduct() == null) {
            throw new ResourceNotFoundException("Product not found with barcode: "+writableProductSpecify.getBarcode());
        }
        ProductSpecify productSpecify = ProductMapper.writableProductSpecifyToProductSpecify(writableProductSpecify);
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.MERCHANT) && !productSpecify.getUser().equals(user)){
            throw new BadRequestException("You don't have permission for product specify with id: "+uuid);
        }
        List<State> states = stateService.findAllByUuids(writableProductSpecify.getStateList());


        productSpecify.setStates(productSpecifyService.allowedStates(productSpecify.getUser(),states));
        productSpecify.setProduct(barcode.getProduct());
        return ProductMapper.productSpecifyToReadableProductSpecify(productSpecifyService.update(uuid,productSpecify));
    }
}
