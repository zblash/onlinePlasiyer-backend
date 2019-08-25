package com.marketing.web.controllers;

import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.services.impl.ProductService;
import com.marketing.web.services.impl.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/photos")
public class PhotosController {

    @Autowired
    private ProductService productService;

    @Autowired
    private StorageService storageService;

    @RequestMapping(value = "/product/{id}", method = RequestMethod.GET,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
        Product product = productService.findById(id);
        byte[] bytes = storageService.loadAsByteArray(product.getPhotoUrl());

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }
}