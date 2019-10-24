package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.product.*;
import com.marketing.web.models.Barcode;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public final class ProductMapper {

    public static Product writableProductToProduct(WritableProduct writableProduct) {
        if (writableProduct == null) {
            return null;
        } else {
            Product product = new Product();
            product.setTax(writableProduct.getTax());
            product.setStatus(writableProduct.isStatus());
            product.setName(writableProduct.getName());
            return product;
        }
    }

    public static ProductSpecify writableProductSpecifyToProductSpecify(WritableProductSpecify writableProductSpecify) {
        if (writableProductSpecify == null) {
            return null;
        } else {
            ProductSpecify productSpecify = new ProductSpecify();
            productSpecify.setTotalPrice(writableProductSpecify.getTotalPrice());
            productSpecify.setUnitPrice(writableProductSpecify.getUnitPrice());
            productSpecify.setQuantity(writableProductSpecify.getQuantity());
            productSpecify.setContents(writableProductSpecify.getContents());
            productSpecify.setUnitType(writableProductSpecify.getUnitType());
            productSpecify.setRecommendedRetailPrice(writableProductSpecify.getRecommendedRetailPrice());
            return productSpecify;
        }
    }

    public static ReadableProduct productToReadableProduct(Product product) {
        if (product == null) {
            return null;
        } else {
            ReadableProduct readableProduct = new ReadableProduct();
            readableProduct.setId(product.getUuid().toString());
            readableProduct.setActive(product.isStatus());
            readableProduct.setBarcodeList(product.getBarcodes().stream().map(Barcode::getBarcodeNo).collect(Collectors.toList()));
            readableProduct.setCategoryName(product.getCategory().getName());
            readableProduct.setName(product.getName());
            readableProduct.setPhotoUrl(product.getPhotoUrl());
            readableProduct.setTax(product.getTax());
            return readableProduct;
        }
    }

    public static ReadableProductSpecify productSpecifyToReadableProductSpecify(ProductSpecify productSpecify) {
        if (productSpecify == null) {
            return null;
        } else {
            ReadableProductSpecify readableProductSpecify = new ReadableProductSpecify();
            readableProductSpecify.setId(productSpecify.getUuid().toString());
            readableProductSpecify.setContents(productSpecify.getContents());
            readableProductSpecify.setQuantity(productSpecify.getQuantity());
            readableProductSpecify.setRecommendedRetailPrice(productSpecify.getRecommendedRetailPrice());
            readableProductSpecify.setTotalPrice(productSpecify.getTotalPrice());
            readableProductSpecify.setUnitPrice(productSpecify.getUnitPrice());
            readableProductSpecify.setUnitType(productSpecify.getUnitType());
            readableProductSpecify.setProductId(productSpecify.getProduct().getUuid().toString());
            readableProductSpecify.setProductName(productSpecify.getProduct().getName());
            readableProductSpecify.setSellerName(productSpecify.getUser().getName());
            readableProductSpecify.setStates(productSpecify.getStates().stream().map((state) -> state.getUuid().toString()).collect(Collectors.toList()));
            return readableProductSpecify;
        }
    }

    public static WrapperReadableProduct pagedProductListToWrapperReadableProduct(Page<Product> pagedProduct){
        if (pagedProduct == null) {
            return null;
        } else {
            WrapperReadableProduct wrapperReadableProduct = new WrapperReadableProduct();
            wrapperReadableProduct.setKey("products");
            wrapperReadableProduct.setTotalPage(pagedProduct.getTotalPages());
            wrapperReadableProduct.setPageNumber(pagedProduct.getNumber()+1);
            if (pagedProduct.hasPrevious()) {
                wrapperReadableProduct.setPreviousPage(pagedProduct.getNumber());
            }
            if (pagedProduct.hasNext()) {
                wrapperReadableProduct.setNextPage(pagedProduct.getNumber()+2);
            }
            wrapperReadableProduct.setFirst(pagedProduct.isFirst());
            wrapperReadableProduct.setLast(pagedProduct.isLast());
            wrapperReadableProduct.setNumberOfElements(pagedProduct.getNumberOfElements());
            wrapperReadableProduct.setTotalElements(pagedProduct.getNumberOfElements());
            wrapperReadableProduct.setProducts(pagedProduct.getContent().stream()
                    .map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
            return wrapperReadableProduct;
        }
    }
}
