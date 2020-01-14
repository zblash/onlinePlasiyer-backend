package com.marketing.web.utils.facade.impl;

import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.*;
import com.marketing.web.repositories.PromotionRepository;
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
    private PromotionRepository promotionRepository;

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
        productSpecify.setCommission(user.getCommission());
        productSpecify.setPromotion(generatePromotion(productSpecify, writableProductSpecify));

        return ProductMapper.productSpecifyToReadableProductSpecify(productSpecifyService.create(productSpecify));
    }

    @Override
    public ReadableProductSpecify updateProductSpecify(String uuid, WritableProductSpecify writableProductSpecify, User user) {
        Barcode barcode = barcodeService.findByBarcodeNo(writableProductSpecify.getBarcode());
        if (barcode == null || barcode.getProduct() == null) {
            throw new ResourceNotFoundException("Product not found with barcode: "+writableProductSpecify.getBarcode());
        }
        ProductSpecify productSpecify;
        RoleType roleType = UserMapper.roleToRoleType(user.getRole());
        if (roleType.equals(RoleType.ADMIN)) {
             productSpecify = productSpecifyService.findByUUID(uuid);
        } else {
            productSpecify = productSpecifyService.findByUUIDAndUser(uuid, user);
        }
        ProductSpecify updatedProductSpecify = ProductMapper.writableProductSpecifyToProductSpecify(writableProductSpecify);

        List<State> states = stateService.findAllByUuids(writableProductSpecify.getStateList());

        updatedProductSpecify.setStates(productSpecifyService.allowedStates(productSpecify.getUser(),states));
        updatedProductSpecify.setProduct(barcode.getProduct());
        updatedProductSpecify.setCommission(user.getCommission());
        updatedProductSpecify.setPromotion(generatePromotion(productSpecify, writableProductSpecify));

        return ProductMapper.productSpecifyToReadableProductSpecify(productSpecifyService.update(productSpecify.getUuid().toString(), updatedProductSpecify));
    }

    private Promotion generatePromotion(ProductSpecify productSpecify, WritableProductSpecify writableProductSpecify){
        if (writableProductSpecify.isDiscount() && writableProductSpecify.getDiscountPercent() > 0
                && writableProductSpecify.getPromotionType() != null && !writableProductSpecify.getPromotionText().isEmpty()){
            Promotion promotion = productSpecify.getPromotion() != null ? productSpecify.getPromotion() : new Promotion();
            promotion.setDiscountPercent(writableProductSpecify.getDiscountPercent());
            promotion.setDiscountUnit(writableProductSpecify.getDiscountUnit() > 0 ? writableProductSpecify.getDiscountUnit() : 1);
            promotion.setPromotionType(writableProductSpecify.getPromotionType());
            promotion.setPromotionText(writableProductSpecify.getPromotionText());
            return promotionRepository.save(promotion);
        }else {
            throw new BadRequestException("Discount percent, type, text must not null or empty");
        }
    }
}
