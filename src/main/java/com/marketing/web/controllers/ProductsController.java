package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.product.*;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.services.category.CategoryService;
import com.marketing.web.services.user.CustomerService;
import com.marketing.web.services.user.MerchantService;
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
@RequestMapping("/private/products")
public class ProductsController {

    private final UserService userService;

    private final ProductService productService;

    private final ProductSpecifyService productSpecifyService;

    private final MerchantService merchantService;

    private final CustomerService customerService;

    private final CategoryService categoryService;

    private final BarcodeService barcodeService;

    private final AmazonClient amazonClient;

    private Logger logger = LoggerFactory.getLogger(ProductsController.class);

    public ProductsController(UserService userService, ProductService productService, ProductSpecifyService productSpecifyService, MerchantService merchantService, CustomerService customerService, CategoryService categoryService, BarcodeService barcodeService, AmazonClient amazonClient) {
        this.userService = userService;
        this.productService = productService;
        this.productSpecifyService = productSpecifyService;
        this.merchantService = merchantService;
        this.customerService = customerService;
        this.categoryService = categoryService;
        this.barcodeService = barcodeService;
        this.amazonClient = amazonClient;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        if (userId == null) {
            return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAll(pageNumber, sortBy, sortType)));
        }
        return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByMerchant(merchantService.findById(userId), pageNumber, sortBy, sortType)));
    }

    @GetMapping("/byUser")
    public ResponseEntity<?> getAllByUser(@RequestParam(required = false) String userId) {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.MERCHANT)) {
            return ResponseEntity.ok(productService.findAllByMerchantWithoutPagination(merchantService.findByUser(user)).stream().map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
        }
        if (userId != null) {
            return ResponseEntity.ok(productService.findAllByMerchantWithoutPagination(merchantService.findByUser(userService.findById(userId))).stream().map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
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
    public ResponseEntity<WrapperPagination<ReadableProduct>> getAllByCategory(@PathVariable String categoryId, @RequestParam(required = false) String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        Category category = categoryService.findById(categoryId);
        if (role.equals(RoleType.ADMIN)) {
            if (userId != null) {
                return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByCategoryAndMerchant(category, merchantService.findByUser(userService.findById(userId)), pageNumber, sortBy, sortType)));
            }
            return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByCategory(category, pageNumber, sortBy, sortType)));
        }
        if (userId != null) {
            return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByCategoryAndMerchantAndStatus(category, merchantService.findByUser(userService.findById(userId)), true, pageNumber, sortBy, sortType)));
        }
        return ResponseEntity.ok(ProductMapper.pagedProductListToWrapperReadableProduct(productService.findAllByCategoryAndStatus(category, true, pageNumber, sortBy, sortType)));
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ReadableProduct> getByBarcode(@PathVariable String barcode) {
        Barcode productBarcode = barcodeService.findByBarcodeNo(barcode);
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
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(productService.findById(id)));
    }

    @GetMapping("/{id}/specifies")
    public ResponseEntity<WrapperPagination<ReadableProductSpecify>> getAllByProduct(@PathVariable String id, @RequestParam(required = false) String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "totalPrice") String sortBy, @RequestParam(defaultValue = "asc") String sortType) {

        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (RoleType.CUSTOMER.equals(role)) {
            if (userId != null) {
                Merchant merchant = merchantService.findById(userId);
                return ResponseEntity.ok(ProductMapper.pagedProductSpecifyListToWrapperReadableProductSpecify(
                        productSpecifyService.findAllByProductAndMerchant(
                                productService.findByUUIDAndMerchant(id, merchant
                                )
                                , merchant, pageNumber, sortBy, sortType)
                ));
            } else {
                return ResponseEntity.ok(
                        ProductMapper
                                .pagedProductSpecifyListToWrapperReadableProductSpecify(
                                        productSpecifyService.findAllByProductAndStates(productService.findById(id), Collections.singletonList(user.getState()), pageNumber, sortBy, sortType)));
            }
        } else if (RoleType.MERCHANT.equals(role)) {
            return ResponseEntity.ok(
                    ProductMapper
                            .pagedProductSpecifyListToWrapperReadableProductSpecify(
                                    productSpecifyService.findAllByProductAndMerchant(productService.findById(id), merchantService.findByUser(user), pageNumber, sortBy, sortType)));
        } else {
            return ResponseEntity.ok(
                    ProductMapper
                            .pagedProductSpecifyListToWrapperReadableProductSpecify(
                                    productSpecifyService.findAllByProduct(productService.findById(id), pageNumber, sortBy, sortType)));
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
                product.setCategory(categoryService.findById(writableProduct.getCategoryId()));
                if (!user.getRole().getName().equals("ROLE_ADMIN")) {
                    product.setStatus(false);
                }
                product = productService.create(product);

                barcode = new Barcode();
                barcode.setBarcodeNo(writableProduct.getBarcode());
                barcode.setProduct(product);
                product.addBarcode(barcodeService.create(barcode));
                if (writableProduct.getCommission() != null) {
                    product.setCommission(writableProduct.getCommission());
                } else {
                    product.setCommission(product.getCategory().getCommission());
                }
                return new ResponseEntity<>(ProductMapper.productToReadableProduct(product), HttpStatus.CREATED);
            }
        }
        throw new BadRequestException("Product already saved");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableProduct> deleteProduct(@PathVariable String id) {
        Product product = productService.findById(id);
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
        product.setCategory(categoryService.findById(writableProduct.getCategoryId()));
        if (writableProduct.getCommission() != null && product.getCommission() != writableProduct.getCommission()) {
            product.setCommission(writableProduct.getCommission());
            List<ProductSpecify> productSpecifies = product.getProductSpecifies().stream()
                    .peek(productSpecify -> productSpecify.setCommission(1))
                    .collect(Collectors.toList());
            productSpecifyService.updateAll(productSpecifies);

        }
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(productService.update(id, product)));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping("/barcode/{id}")
    public ResponseEntity<ReadableProduct> addBarcode(@PathVariable String id, @Valid @RequestBody WritableBarcode writableBarcode) {
        Product product = productService.findById(id);
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
    @DeleteMapping("/barcode/{id}")
    public ResponseEntity<ReadableProduct> removeBarcode(@PathVariable String id, @Valid @RequestBody WritableBarcode writableBarcode) {
        Product product = productService.findById(id);
        Barcode barcode = barcodeService.findByProductAndBarcodeNo(product, writableBarcode.getBarcode());
        product.removeBarcode(barcode);
        barcodeService.delete(barcode);
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(product));
    }

}
