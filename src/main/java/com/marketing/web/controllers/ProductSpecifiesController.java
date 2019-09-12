package com.marketing.web.controllers;

import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.pubsub.ProductProducer;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.services.product.ProductService;
import com.marketing.web.services.product.ProductServiceImpl;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.product.ProductSpecifyServiceImpl;
import com.marketing.web.services.user.UserService;
import com.marketing.web.services.user.UserServiceImpl;
import com.marketing.web.utils.facade.ProductFacade;
import com.marketing.web.utils.mappers.ProductMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products/specify")
public class ProductSpecifiesController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    @Autowired
    private ProductProducer productProducer;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private StateRepository stateRepository;


    @Autowired
    private ProductFacade productFacade;

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<ReadableProductSpecify>> getAll(){
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.MERCHANT)){
            return ResponseEntity.ok(productSpecifyService.findAllByUser(user).stream()
                    .map(ProductMapper::productSpecifyToReadableProductSpecify).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(productSpecifyService.findAll().stream()
                .map(ProductMapper::productSpecifyToReadableProductSpecify).collect(Collectors.toList()));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<List<ReadableProductSpecify>> getAllByProduct(@PathVariable String id){
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        Product product = productService.findByUUID(id);

        switch (role){
            case MERCHANT:
               return ResponseEntity.ok(productSpecifyService.findAllByProductAndStates(product,user.getActiveStates()).stream()
                        .map(ProductMapper::productSpecifyToReadableProductSpecify).collect(Collectors.toList()));
            case CUSTOMER:
               return ResponseEntity.ok(productSpecifyService.findAllByProductAndStates(product, Collections.singletonList(user.getAddress().getState())).stream()
                        .map(ProductMapper::productSpecifyToReadableProductSpecify).collect(Collectors.toList()));
            default:
                return ResponseEntity.ok(productSpecifyService.findAllByProduct(product).stream()
                        .map(ProductMapper::productSpecifyToReadableProductSpecify).collect(Collectors.toList()));
        }

    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ReadableProductSpecify> createProductSpecify(@Valid @RequestBody WritableProductSpecify writableProductSpecify, @RequestParam(required = false) String userId){
        User user = userService.getLoggedInUser();

        ReadableProductSpecify readableProductSpecify;
        if (user.getRole().getName().equals("ROLE_"+ RoleType.ADMIN) && !userId.isEmpty()) {
            readableProductSpecify = productFacade.createProductSpecify(writableProductSpecify, userService.findByUUID(userId));
        }else if (user.getRole().getName().equals("ROLE_"+RoleType.MERCHANT)){
            readableProductSpecify = productFacade.createProductSpecify(writableProductSpecify, user);
        }else{
            throw new BadRequestException("userId request parameter must not blank");
        }
//        product.addProductSpecify(productSpecify);
//        productProducer.sendProduct(product.getId());
        return ResponseEntity.ok(readableProductSpecify);
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ReadableProductSpecify> deleteProductSpecify(@PathVariable String id){
        User user = userService.getLoggedInUser();
        ProductSpecify productSpecify;
        if (user.getRole().getName().equals("ROLE_ADMIN")){
            productSpecify = productSpecifyService.findByUUID(id);
        }else {
            productSpecify = productSpecifyService.findByUUIDAndUser(id,user);
        }
        productSpecifyService.delete(productSpecify);
        return ResponseEntity.ok(ProductMapper.productSpecifyToReadableProductSpecify(productSpecify));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ReadableProductSpecify> updateProductSpecify(@PathVariable String id, @Valid @RequestBody WritableProductSpecify writableProductSpecify, @RequestParam(required = false) String userId){
        User user = userService.getLoggedInUser();
        ReadableProductSpecify readableProductSpecify;

        if (user.getRole().getName().equals("ROLE_"+ RoleType.ADMIN) && !userId.isEmpty()) {
            readableProductSpecify = productFacade.updateProductSpecify(id,writableProductSpecify,userService.findByUUID(userId));
        }else if (user.getRole().getName().equals("ROLE_"+RoleType.MERCHANT)){
            readableProductSpecify = productFacade.updateProductSpecify(id,writableProductSpecify,user);
        }else{
            throw new BadRequestException("userId request parameter must not blank");
        }
//        product.addProductSpecify(productSpecify);
//        productProducer.sendProduct(product.getId());
        return ResponseEntity.ok(readableProductSpecify);
    }
}
