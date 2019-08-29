package com.marketing.web.controllers;

import com.marketing.web.dtos.product.ProductDTO;
import com.marketing.web.dtos.product.ProductSpecifyDTO;
import com.marketing.web.security.CustomPrincipal;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.User;
import com.marketing.web.pubsub.ProductProducer;
import com.marketing.web.pubsub.ProductSubscriber;
import com.marketing.web.services.product.ProductService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.storage.StorageService;
import com.marketing.web.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    @Autowired
    private ProductSubscriber productSubscriber;

    @Autowired
    private ProductProducer productProducer;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll(){
        return ResponseEntity.ok(productService.findAllByStatus(true));
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<Product>> getAllByCategory(@PathVariable Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        String userState = user.getAddress().getState();
        List<Product> products = productService.findByCategory(id).stream().filter(Product::isStatus).collect(Collectors.toList());
        return ResponseEntity.ok(productService.filterByState(products, userState));

    }

    @GetMapping("/bybarcode/{barcode}")
    public ResponseEntity<Product> getByBarcode(@PathVariable String barcode){
        return ResponseEntity.ok(productService.findByBarcode(barcode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id){
        return ResponseEntity.ok(productService.findById(id));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@Valid ProductDTO productDTO, @RequestParam(value="uploadfile", required = true) final MultipartFile uploadfile){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        Product product = productService.findByBarcode(productDTO.getBarcode());
        if (product == null){
            String fileName = storageService.store(uploadfile);
            productDTO.setPhotoUrl(fileName);
            if (!user.getRole().getName().equals("ROLE_ADMIN")){
                productDTO.setStatus(false);
            }
            return ResponseEntity.ok(productService.create(productDTO));
        }

        return ResponseEntity.ok("Product already added in system");

    }

    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping("/specify/create")
    public ResponseEntity<?> createProductSpecify(@Valid @RequestBody ProductSpecifyDTO productSpecifyDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        Product product = productService.findByBarcode(productSpecifyDTO.getBarcode());
        if (product == null){
            return ResponseEntity.ok("There is no product with this barcode "+productSpecifyDTO.getBarcode());
        }
        ProductSpecify productSpecify = productSpecifyService.create(productSpecifyDTO,product,user);

        product.addProductSpecify(productSpecify);
        productProducer.sendProduct(product.getId());
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public Map<String,Product> deleteProduct(@PathVariable(value = "id") Long id){
        Product product = productService.findById(id);
        productService.delete(product);
        Map<String,Product> response = new HashMap<>();
        response.put("deleted",product);
        return response;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(value = "id") Long id,@Valid @RequestBody Product updatedProduct){
        return ResponseEntity.ok(productService.update(productService.findById(id),updatedProduct));
    }

    @GetMapping("/live")
    public SseEmitter subscribe(){
        return productSubscriber.subscribe();
    }
}
