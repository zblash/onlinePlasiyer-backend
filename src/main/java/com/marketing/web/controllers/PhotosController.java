package com.marketing.web.controllers;

import com.marketing.web.models.Category;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.services.impl.CategoryService;
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
    private StorageService storageService;

    @RequestMapping(value = "/{name}", method = RequestMethod.GET,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getProductImage(@PathVariable String name) throws IOException {
        byte[] bytes = storageService.loadAsByteArray(name);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }
}
