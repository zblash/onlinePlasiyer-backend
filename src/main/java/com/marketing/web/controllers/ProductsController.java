package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.product.*;
import com.marketing.web.enums.CommissionType;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.services.category.CategoryService;
import com.marketing.web.services.product.BarcodeService;
import com.marketing.web.services.product.ProductService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.storage.AmazonClient;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.ProductMapper;
import com.marketing.web.utils.mappers.UserMapper;
import com.marketing.web.validations.ValidImg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private AmazonClient amazonClient;

    private Logger logger = LoggerFactory.getLogger(ProductsController.class);

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        if (userId.isEmpty()) {
            return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAll(pageNumber, sortBy, sortType)));
        }
        return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByUser(userService.findByUUID(userId), pageNumber, sortBy, sortType)));
    }

    @GetMapping("/byUser")
    public ResponseEntity<?> getAllByUser(@RequestParam(required = false) String userId) {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.MERCHANT)){
            return ResponseEntity.ok(productService.findAllByUserWithoutPagination(user).stream().map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
        }
        if (!userId.isEmpty()) {
            return ResponseEntity.ok(productService.findAllByUserWithoutPagination(userService.findByUUID(userId)).stream().map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(productService.findAllWithoutPagination("id", "desc").stream().map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
    }

    @GetMapping("/actives")
    public ResponseEntity<WrapperPagination<ReadableProduct>> getAllActives(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByStatus(true, pageNumber, sortBy, sortType)));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ReadableProduct>> getByFilter(@RequestParam String name) {
        return ResponseEntity.ok(productService.simpleFilterByName(name).stream().map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<WrapperPagination<ReadableProduct>> getAllByCategory(@PathVariable String categoryId, @RequestParam(defaultValue = "") String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        Category category = categoryService.findByUUID(categoryId);
        if (role.equals(RoleType.ADMIN)) {
            if (!userId.isEmpty()){
                return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByCategoryAndUser(category, userService.findByUUID(userId), pageNumber, sortBy, sortType)));
            }
            return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByCategory(category, pageNumber, sortBy, sortType)));
        }
        if (!userId.isEmpty()){
            return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByCategoryAndUserAndStatus(category, userService.findByUUID(userId), true, pageNumber, sortBy, sortType)));
        }
        return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByCategoryAndStatus(category, true, pageNumber, sortBy, sortType)));
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ReadableProduct> getByBarcode(@PathVariable String barcode) {
        Barcode productBarcode = barcodeService.findByBarcodeNo(barcode);
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(productBarcode.getProduct()));
    }

    //TODO degistirilecek
    @PostMapping("/checkProduct/{barcode}")
    public ResponseEntity<?> checkProductByBarcode(@PathVariable String barcode) {
        Barcode productBarcode = barcodeService.findByBarcodeNo(barcode);
        Product product = productBarcode.getProduct();
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(productBarcode.getProduct()));
    }

    @PostMapping("/hasProduct/{barcode}")
    public ResponseEntity<Boolean> hasProductByBarcode(@PathVariable String barcode) {
        Barcode productBarcode = barcodeService.checkByBarcodeNo(barcode);
        if (productBarcode == null || productBarcode.getProduct() == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadableProduct> getById(@PathVariable String id) {
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(productService.findByUUID(id)));
    }

    @GetMapping("/{id}/specifies")
    public ResponseEntity<WrapperPagination<ReadableProductSpecify>> getAllByProduct(@PathVariable String id, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "totalPrice") String sortBy, @RequestParam(defaultValue = "asc") String sortType) {

        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        switch (role) {
            case CUSTOMER:
                return ResponseEntity.ok(
                        ProductMapper
                                .pagedProductSpecifyListToWrapperReadableProductSpecify(
                                        productSpecifyService.findAllByProductAndStates(productService.findByUUID(id), Collections.singletonList(user.getAddress().getState()), pageNumber, sortBy, sortType)));
            case MERCHANT:
                return ResponseEntity.ok(
                        ProductMapper
                                .pagedProductSpecifyListToWrapperReadableProductSpecify(
                                        productSpecifyService.findAllByProductAndUser(productService.findByUUID(id), user, pageNumber, sortBy, sortType)));
            default:
                return ResponseEntity.ok(
                        ProductMapper
                                .pagedProductSpecifyListToWrapperReadableProductSpecify(
                                        productSpecifyService.findAllByProduct(productService.findByUUID(id), pageNumber, sortBy, sortType)));
        }

    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid WritableProduct writableProduct, @ValidImg @RequestParam(required = false) final MultipartFile uploadedFile) {
        User user = userService.getLoggedInUser();

        Barcode barcode = barcodeService.checkByBarcodeNo(writableProduct.getBarcode());
        if (barcode == null) {
            Product product = productService.findByName(writableProduct.getName());
            if (product == null) {
                product = ProductMapper.writableProductToProduct(writableProduct);
                if (uploadedFile != null && !uploadedFile.isEmpty()) {
                    String fileUrl = amazonClient.uploadFile(uploadedFile);
                    product.setPhotoUrl(fileUrl);
                }
                product.setCategory(categoryService.findByUUID(writableProduct.getCategoryId()));
                if (!user.getRole().getName().equals("ROLE_ADMIN")) {
                    product.setStatus(false);
                }
                product = productService.create(product);
                barcode = new Barcode();
                barcode.setBarcodeNo(writableProduct.getBarcode());
                barcode.setProduct(product);
                product.addBarcode(barcodeService.create(barcode));
            } else {
                barcode = new Barcode();
                barcode.setBarcodeNo(writableProduct.getBarcode());
                barcode.setProduct(product);
                product.addBarcode(barcodeService.create(barcode));
            }

            return new ResponseEntity<>(ProductMapper.productToReadableProduct(product), HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Product already added", HttpStatus.CONFLICT);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableProduct> deleteProduct(@PathVariable String id) {
        Product product = productService.findByUUID(id);
        amazonClient.deleteFileFromS3Bucket(product.getPhotoUrl());
        productService.delete(product);
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(product));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableProduct> updateProduct(@PathVariable String id, @Valid WritableProduct writableProduct, @ValidImg @RequestParam(required = false) MultipartFile uploadedFile) {
        Product product = barcodeService.findByBarcodeNo(writableProduct.getBarcode()).getProduct();
        if (uploadedFile != null && !uploadedFile.isEmpty()) {
            if (!product.getPhotoUrl().isEmpty()) {
                amazonClient.deleteFileFromS3Bucket(product.getPhotoUrl());
            }
            String fileUrl = amazonClient.uploadFile(uploadedFile);
            product.setPhotoUrl(fileUrl);
        }
        product.setName(writableProduct.getName());
        product.setStatus(writableProduct.isStatus());
        product.setTax(writableProduct.getTax());
        product.setCategory(categoryService.findByUUID(writableProduct.getCategoryId()));
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(productService.update(id, product)));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping("/addBarcode/{id}")
    public ResponseEntity<ReadableProduct> addBarcode(@PathVariable String id, @Valid @RequestBody WritableBarcode writableBarcode) {
        Product product = productService.findByUUID(id);
        if (barcodeService.checkByBarcodeNo(writableBarcode.getBarcode()) == null) {
            Barcode barcode = new Barcode();
            barcode.setBarcodeNo(writableBarcode.getBarcode());
            barcode.setProduct(product);
            product.addBarcode(barcodeService.create(barcode));
            return ResponseEntity.ok(ProductMapper.productToReadableProduct(product));
        }
        throw new BadRequestException("This barcode already added : " + writableBarcode.getBarcode());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/removeBarcode/{id}")
    public ResponseEntity<ReadableProduct> removeBarcode(@PathVariable String id, @Valid @RequestBody WritableBarcode writableBarcode) {
        Product product = productService.findByUUID(id);
        Barcode barcode = barcodeService.findByProductAndBarcodeNo(product, writableBarcode.getBarcode());
        product.removeBarcode(barcode);
        barcodeService.delete(barcode);
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(product));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/commissions")
    public ResponseEntity<List<ReadableProductSpecify>> setCommissions(@Valid @RequestBody WritableCommission writableCommission) {
        List<ProductSpecify> productSpecifyList = null;
        if (writableCommission.getCommissionType().equals(CommissionType.ALL)) {
            productSpecifyList = productSpecifyService.findAllWithoutPagination();
        } else if (writableCommission.getId() != null) {
            if (writableCommission.getCommissionType().equals(CommissionType.PRD)) {
                productSpecifyList = productSpecifyService.findAllByProductWithoutPagination(productService.findByUUID(writableCommission.getId()));
            } else if (writableCommission.getCommissionType().equals(CommissionType.USER)) {
                User user = userService.findByUUID(writableCommission.getId());
                if (!UserMapper.roleToRoleType(user.getRole()).equals(RoleType.MERCHANT)){
                    throw new BadRequestException("Commission not available for this user");
                }
                user.setCommission(writableCommission.getCommission());
                userService.update(user.getId(), user);
                productSpecifyList = productSpecifyService.findAllByUserWithoutPagination(user);
            }
        } else {
            throw new BadRequestException("Commission type or id must not null");
        }
        productSpecifyList.stream().forEach(productSpecify -> productSpecify.setCommission(writableCommission.getCommission()));
        return ResponseEntity.ok(productSpecifyService.updateAll(productSpecifyList).stream()
                .map(ProductMapper::productSpecifyToReadableProductSpecify).collect(Collectors.toList()));
    }
}
