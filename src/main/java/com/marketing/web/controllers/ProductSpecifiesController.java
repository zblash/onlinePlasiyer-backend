package com.marketing.web.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.marketing.web.dtos.WrapperPagination;
import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.enums.RoleType;
import com.marketing.web.enums.WsStatus;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.pubsub.ProductProducer;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.facade.ProductFacade;
import com.marketing.web.utils.mappers.ProductMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/products/specify")
public class ProductSpecifiesController {

    private Logger logger = LoggerFactory.getLogger(ProductSpecifiesController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductProducer productProducer;

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableProductSpecify>> getAll(@RequestParam(required = false) Integer pageNumber) {
        if (pageNumber == null) {
            pageNumber = 1;
        }
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.MERCHANT)) {
            return ResponseEntity.ok(
                    ProductMapper
                            .pagedProductSpecifyListToWrapperReadableProductSpecify(productSpecifyService.findAllByUser(user, pageNumber)));
        }

        return ResponseEntity.ok(
                ProductMapper
                        .pagedProductSpecifyListToWrapperReadableProductSpecify(productSpecifyService.findAll(pageNumber)));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ReadableProductSpecify> getById(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.ADMIN)) {
            return ResponseEntity.ok(ProductMapper.productSpecifyToReadableProductSpecify(productSpecifyService.findByUUID(id)));
        }
        return ResponseEntity.ok(ProductMapper.productSpecifyToReadableProductSpecify(productSpecifyService.findByUUIDAndUser(id, user)));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/byUser/{userId}")
    public ResponseEntity<WrapperPagination<ReadableProductSpecify>> getAllByUser(@PathVariable String userId, @RequestParam(required = false) Integer pageNumber) {
        if (pageNumber == null) {
            pageNumber = 1;
        }
        User user = userService.findByUUID(userId);
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.MERCHANT)) {
            return ResponseEntity.ok(
                    ProductMapper
                            .pagedProductSpecifyListToWrapperReadableProductSpecify(productSpecifyService.findAllByUser(user, pageNumber)));

        }
        throw new BadRequestException("User does not have merchant role");
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ReadableProductSpecify> createProductSpecify(@Valid @RequestBody WritableProductSpecify writableProductSpecify, @RequestParam(required = false) String userId) throws JsonProcessingException {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        ReadableProductSpecify readableProductSpecify;
        if (role.equals(RoleType.MERCHANT)) {
            readableProductSpecify = productFacade.createProductSpecify(writableProductSpecify, user);
        } else if (!userId.isEmpty()) {
            readableProductSpecify = productFacade.createProductSpecify(writableProductSpecify, userService.findByUUID(userId));
        } else {
            throw new BadRequestException("userId request parameter must not blank");
        }

        productProducer.sendProductSpecify(ProductMapper.readableProductSpecifyToWrapperWsProductSpecify(readableProductSpecify, WsStatus.CR));

        return new ResponseEntity<>(readableProductSpecify, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableProductSpecify> deleteProductSpecify(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        ProductSpecify productSpecify;
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.ADMIN)) {
            productSpecify = productSpecifyService.findByUUID(id);
        } else {
            productSpecify = productSpecifyService.findByUUIDAndUser(id, user);
        }
        productSpecifyService.delete(productSpecify);
        return ResponseEntity.ok(ProductMapper.productSpecifyToReadableProductSpecify(productSpecify));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableProductSpecify> updateProductSpecify(@PathVariable String id, @Valid @RequestBody WritableProductSpecify writableProductSpecify) throws JsonProcessingException {
        User user = userService.getLoggedInUser();
        ReadableProductSpecify readableProductSpecify = productFacade.updateProductSpecify(id, writableProductSpecify, user);

        productProducer.sendProductSpecify(ProductMapper.readableProductSpecifyToWrapperWsProductSpecify(readableProductSpecify, WsStatus.UP));
        return ResponseEntity.ok(readableProductSpecify);
    }
}
