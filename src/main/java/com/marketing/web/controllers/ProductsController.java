package com.marketing.web.controllers;

import com.marketing.web.dtos.product.ReadableProduct;
import com.marketing.web.dtos.product.WritableProduct;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.models.User;
import com.marketing.web.pubsub.ProductProducer;
import com.marketing.web.pubsub.ProductSubscriber;
import com.marketing.web.services.category.CategoryService;
import com.marketing.web.services.category.CategoryServiceImpl;
import com.marketing.web.services.product.ProductService;
import com.marketing.web.services.product.ProductServiceImpl;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.product.ProductSpecifyServiceImpl;
import com.marketing.web.services.storage.StorageService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.services.user.UserServiceImpl;
import com.marketing.web.utils.mappers.ProductMapper;
import com.marketing.web.utils.mappers.UserMapper;
import com.marketing.web.validations.ValidImg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
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
    private CategoryService categoryService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    @Autowired
    private ProductSubscriber productSubscriber;

    @Autowired
    private ProductProducer productProducer;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public ResponseEntity<List<ReadableProduct>> getAll(){
        return ResponseEntity.ok(productService.findAll().stream()
                .map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
    }

    @GetMapping("/actives")
    public ResponseEntity<List<ReadableProduct>> getAllActives(){
        return ResponseEntity.ok(productService.findAllByStatus(true).stream()
                .map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ReadableProduct>> getAllByCategory(@PathVariable String categoryId){
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        Category category = categoryService.findByUUID(categoryId);
        if (role.equals(RoleType.ADMIN)){
            return ResponseEntity.ok(productService.findAllByCategory(category).stream()
                    .map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
        }

        return ResponseEntity.ok(productService.findAllByCategoryAndStatus(category,true).stream()
                .map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));

    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ReadableProduct> getByBarcode(@PathVariable String barcode) {
        if (productService.findByBarcode(barcode) != null) {
            return ResponseEntity.ok(ProductMapper.productToReadableProduct(productService.findByBarcode(barcode)));
        }
        throw new ResourceNotFoundException("Product not found with barcode: "+barcode);
    }

    //TODO degistirilecek
    @PostMapping("/checkProduct/{barcode}")
    public ResponseEntity<?> checkProductByBarcode(@PathVariable String barcode) {
        if (productService.findByBarcode(barcode) != null) {
            return ResponseEntity.ok(ProductMapper.productToReadableProduct(productService.findByBarcode(barcode)));
        }
        throw new ResourceNotFoundException("Product not found with barcode: "+barcode);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadableProduct> getById(@PathVariable String id){
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(productService.findByUUID(id)));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@Valid WritableProduct writableProduct,@ValidImg @RequestParam(value="uploadfile", required = true) final MultipartFile uploadfile){
        User user = userService.getLoggedInUser();
        Product product = productService.findByBarcode(writableProduct.getBarcode());

        if (product == null){
            product = ProductMapper.writableProductToProduct(writableProduct);
            String fileName = storageService.store(uploadfile);
            product.setPhotoUrl("http://localhost:8080/photos/"+fileName);
            product.setCategory(categoryService.findByUUID(writableProduct.getCategoryId()));
            if (!user.getRole().getName().equals("ROLE_ADMIN")){
                product.setStatus(false);
            }

            return ResponseEntity.ok(ProductMapper.productToReadableProduct(productService.create(product)));
        }

        return new ResponseEntity<>("Product already added", HttpStatus.CONFLICT);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ReadableProduct> deleteProduct(@PathVariable String id){
        Product product = productService.findByUUID(id);
        productService.delete(product);
        return ResponseEntity.ok(ProductMapper.productToReadableProduct(product));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ReadableProduct> updateProduct(@PathVariable String id, @Valid WritableProduct writableProduct, @ValidImg @RequestParam(value="uploadfile", required = false) final MultipartFile uploadfile){
        Product product = ProductMapper.writableProductToProduct(writableProduct);
        if (uploadfile != null && !uploadfile.isEmpty()) {
            String fileName = storageService.store(uploadfile);
            product.setPhotoUrl("http://localhost:8080/photos/"+fileName);
        }

        product.setCategory(categoryService.findByUUID(writableProduct.getCategoryId()));


        return ResponseEntity.ok(ProductMapper.productToReadableProduct(productService.update(id,product)));
    }

    @GetMapping("/live")
    public SseEmitter subscribe(){
        return productSubscriber.subscribe();
    }
}
