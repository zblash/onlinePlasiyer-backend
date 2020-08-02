package com.marketing.web.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.pubsub.ProductProducer;
import com.marketing.web.services.user.MerchantService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.facade.ProductFacade;
import com.marketing.web.utils.mappers.ProductMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/private/products/specify")
public class ProductSpecifiesController {

    private Logger logger = LoggerFactory.getLogger(ProductSpecifiesController.class);

    private final UserService userService;

    private final ProductSpecifyService productSpecifyService;

    private final ProductFacade productFacade;

    private final MerchantService merchantService;

    private final ProductProducer productProducer;

    public ProductSpecifiesController(UserService userService, ProductSpecifyService productSpecifyService, ProductFacade productFacade, MerchantService merchantService, ProductProducer productProducer) {
        this.userService = userService;
        this.productSpecifyService = productSpecifyService;
        this.productFacade = productFacade;
        this.merchantService = merchantService;
        this.productProducer = productProducer;
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableProductSpecify>> getAll(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "totalPrice") String sortBy, @RequestParam(defaultValue = "asc") String sortType) {

        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.MERCHANT)) {
            return ResponseEntity.ok(
                    ProductMapper
                            .pagedProductSpecifyListToWrapperReadableProductSpecify(productSpecifyService.findAllByMerchant(merchantService.getLoggedInMerchant(), pageNumber, sortBy, sortType)));
        }

        return ResponseEntity.ok(
                ProductMapper
                        .pagedProductSpecifyListToWrapperReadableProductSpecify(productSpecifyService.findAll(pageNumber, sortBy, sortType)));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ReadableProductSpecify> getById(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.ADMIN)) {
            return ResponseEntity.ok(ProductMapper.productSpecifyToReadableProductSpecify(productSpecifyService.findById(id)));
        }
        return ResponseEntity.ok(ProductMapper.productSpecifyToReadableProductSpecify(productSpecifyService.findByIdAndMerchant(id, merchantService.getLoggedInMerchant())));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/byUser/{userId}")
    public ResponseEntity<WrapperPagination<ReadableProductSpecify>> getAllByUser(@PathVariable String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "totalPrice") String sortBy, @RequestParam(defaultValue = "asc") String sortType) {

        Merchant merchant = merchantService.findById(userId);
            return ResponseEntity.ok(
                    ProductMapper
                            .pagedProductSpecifyListToWrapperReadableProductSpecify(productSpecifyService.findAllByMerchant(merchant, pageNumber, sortBy, sortType)));

    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ReadableProductSpecify> createProductSpecify(@Valid @RequestBody WritableProductSpecify writableProductSpecify, @RequestParam(required = false) String userId) throws JsonProcessingException {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        ReadableProductSpecify readableProductSpecify;
        if (role.equals(RoleType.MERCHANT)) {
            readableProductSpecify = productFacade.createProductSpecify(writableProductSpecify, merchantService.getLoggedInMerchant());
        } else if (userId != null && !userId.isEmpty()) {
            readableProductSpecify = productFacade.createProductSpecify(writableProductSpecify, merchantService.findById(userId));
        } else {
            throw new BadRequestException("userId request parameter must not blank");
        }

//        productProducer.sendProductSpecify(ProductMapper.readableProductSpecifyToWrapperWsProductSpecify(readableProductSpecify, WsStatus.CR));

        return new ResponseEntity<>(readableProductSpecify, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableProductSpecify> deleteProductSpecify(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        ProductSpecify productSpecify;
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.ADMIN)) {
            productSpecify = productSpecifyService.findById(id);
        } else {
            productSpecify = productSpecifyService.findByIdAndMerchant(id, merchantService.getLoggedInMerchant());
        }
        productSpecifyService.delete(productSpecify);
        return ResponseEntity.ok(ProductMapper.productSpecifyToReadableProductSpecify(productSpecify));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableProductSpecify> updateProductSpecify(@PathVariable String id, @RequestParam(required = false) String userId, @Valid @RequestBody WritableProductSpecify writableProductSpecify) throws JsonProcessingException {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        ReadableProductSpecify readableProductSpecify;
        if (role.equals(RoleType.MERCHANT)) {
            readableProductSpecify = productFacade.updateProductSpecify(id, writableProductSpecify, merchantService.getLoggedInMerchant());
        } else if (userId != null && !userId.isEmpty()) {
            readableProductSpecify = productFacade.updateProductSpecify(id, writableProductSpecify, merchantService.findById(userId));
        } else {
            throw new BadRequestException("userId request parameter must not blank");
        }
//        productProducer.sendProductSpecify(ProductMapper.readableProductSpecifyToWrapperWsProductSpecify(readableProductSpecify, WsStatus.UP));
        return ResponseEntity.ok(readableProductSpecify);
    }
}
