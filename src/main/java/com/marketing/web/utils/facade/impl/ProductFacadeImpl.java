package com.marketing.web.utils.facade.impl;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.enums.PromotionType;
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

import java.math.BigDecimal;
import java.util.List;

@Service(value = "productFacade")
public class ProductFacadeImpl implements ProductFacade {

    private final ProductService productService;

    private final BarcodeService barcodeService;

    private final ProductSpecifyService productSpecifyService;

    private final PromotionRepository promotionRepository;

    private final StateService stateService;

    public ProductFacadeImpl(ProductService productService, BarcodeService barcodeService, ProductSpecifyService productSpecifyService, PromotionRepository promotionRepository, StateService stateService) {
        this.productService = productService;
        this.barcodeService = barcodeService;
        this.productSpecifyService = productSpecifyService;
        this.promotionRepository = promotionRepository;
        this.stateService = stateService;
    }

    @Override
    public ReadableProductSpecify createProductSpecify(WritableProductSpecify writableProductSpecify, Merchant merchant) {
        Barcode barcode = barcodeService.findByBarcodeNo(writableProductSpecify.getBarcode());
        if (barcode == null || barcode.getProduct() == null) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND + "product.barcode", writableProductSpecify.getBarcode());
        }

        Product product = barcode.getProduct();
        ProductSpecify productSpecify = ProductMapper.writableProductSpecifyToProductSpecify(writableProductSpecify);

        List<State> states = stateService.findAllByIds(writableProductSpecify.getStateList());
        if (states.isEmpty()) {
            throw new BadRequestException("There is no state with sent id list");
        }
        productSpecify.setProduct(product);
        productSpecify.setMerchant(merchant);
        productSpecify.setStates(productSpecifyService.allowedStates(merchant, states));
        double commission = merchant.getCommission() != 0.0 ? merchant.getCommission() : (product.getCommission() != 0.0 ? product.getCommission() : product.getCategory().getCommission());
        productSpecify.setCommission(commission);
        if (writableProductSpecify.isDiscount()) {
            productSpecify.setPromotion(populatePromotion(productSpecify, writableProductSpecify));
        }
        product.addMerchant(merchant);
        productService.update(product.getId().toString(), product);
        return ProductMapper.productSpecifyToReadableProductSpecify(productSpecifyService.create(productSpecify));

    }

    @Override
    public ReadableProductSpecify updateProductSpecify(String uuid, WritableProductSpecify writableProductSpecify, Merchant merchant) {
        Barcode barcode = barcodeService.findByBarcodeNo(writableProductSpecify.getBarcode());
        if (barcode == null || barcode.getProduct() == null) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND + "product.barcode", writableProductSpecify.getBarcode());
        }
        ProductSpecify productSpecify = productSpecifyService.findByIdAndMerchant(uuid, merchant);

        Product product = barcode.getProduct();
        ProductSpecify updatedProductSpecify = ProductMapper.writableProductSpecifyToProductSpecify(writableProductSpecify);

        List<State> states = stateService.findAllByIds(writableProductSpecify.getStateList());

        updatedProductSpecify.setStates(productSpecifyService.allowedStates(productSpecify.getMerchant(), states));
        updatedProductSpecify.setProduct(product);
        double commission = merchant.getCommission() != 0.0 ? merchant.getCommission() : (product.getCommission() != 0.0 ? product.getCommission() : product.getCategory().getCommission());
        updatedProductSpecify.setCommission(commission);
        if (writableProductSpecify.isDiscount()) {
            updatedProductSpecify.setPromotion(populatePromotion(productSpecify, writableProductSpecify));
        }
        product.addMerchant(merchant);
        productService.update(product.getId().toString(), product);
        return ProductMapper.productSpecifyToReadableProductSpecify(productSpecifyService.update(productSpecify.getId().toString(), updatedProductSpecify));
    }

    private Promotion populatePromotion(ProductSpecify productSpecify, WritableProductSpecify writableProductSpecify) {
        if (!writableProductSpecify.getPromotionText().isEmpty()
                && writableProductSpecify.getDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
            Promotion promotion = productSpecify.getPromotion() != null ? productSpecify.getPromotion() : new Promotion();
            promotion.setDiscountUnit(writableProductSpecify.getDiscountUnit() > 0 ? writableProductSpecify.getDiscountUnit() : 1);
            promotion.setDiscountValue(writableProductSpecify.getDiscountValue());
            promotion.setPromotionText(writableProductSpecify.getPromotionText());
            return promotionRepository.save(promotion);
        } else {
            throw new BadRequestException("Discount percent, text must not null or empty");
        }
    }

}
