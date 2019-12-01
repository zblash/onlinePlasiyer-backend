package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.WrapperPagination;
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
            readableProduct.setCategoryId(product.getCategory().getUuid().toString());
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
            readableProductSpecify.setStates(productSpecify.getStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
            return readableProductSpecify;
        }
    }

    public static WrapperPagination<ReadableProduct> pagedProductListToWrapperReadableProduct(Page<Product> pagedProduct){
        if (pagedProduct == null) {
            return null;
        } else {
            WrapperPagination<ReadableProduct> wrapperReadableProduct = new WrapperPagination<>();
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
            wrapperReadableProduct.setElementCountOfPage(pagedProduct.getNumberOfElements());
            wrapperReadableProduct.setTotalElements(pagedProduct.getTotalElements());
            wrapperReadableProduct.setValues(pagedProduct.getContent().stream()
                    .map(ProductMapper::productToReadableProduct).collect(Collectors.toList()));
            return wrapperReadableProduct;
        }
    }

    public static WrapperPagination<ReadableProductSpecify> pagedProductSpecifyListToWrapperReadableProductSpecify(Page<ProductSpecify> pagedProductSpecify){
        if (pagedProductSpecify == null) {
            return null;
        } else {
            WrapperPagination<ReadableProductSpecify> wrapperReadableProductSpecify = new WrapperPagination<>();
            wrapperReadableProductSpecify.setKey("productSpecifies");
            wrapperReadableProductSpecify.setTotalPage(pagedProductSpecify.getTotalPages());
            wrapperReadableProductSpecify.setPageNumber(pagedProductSpecify.getNumber()+1);
            if (pagedProductSpecify.hasPrevious()) {
                wrapperReadableProductSpecify.setPreviousPage(pagedProductSpecify.getNumber());
            }
            if (pagedProductSpecify.hasNext()) {
                wrapperReadableProductSpecify.setNextPage(pagedProductSpecify.getNumber()+2);
            }
            wrapperReadableProductSpecify.setFirst(pagedProductSpecify.isFirst());
            wrapperReadableProductSpecify.setLast(pagedProductSpecify.isLast());
            wrapperReadableProductSpecify.setElementCountOfPage(pagedProductSpecify.getNumberOfElements());
            wrapperReadableProductSpecify.setTotalElements(pagedProductSpecify.getTotalElements());
            wrapperReadableProductSpecify.setValues(pagedProductSpecify.getContent().stream()
                    .map(ProductMapper::productSpecifyToReadableProductSpecify).collect(Collectors.toList()));
            return wrapperReadableProductSpecify;
        }
    }
}
