package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.product.ReadableProduct;
import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.dtos.product.WritableProduct;
import com.marketing.web.dtos.product.WritableProductSpecify;
import com.marketing.web.dtos.ticket.ReadableTicket;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper( ProductMapper.class );

    @InheritInverseConfiguration
    Product writableProductToProduct(WritableProduct writableProduct);

    @InheritInverseConfiguration
    ProductSpecify writableProductSpecifyToProductSpecify(WritableProductSpecify writableProductSpecify);

    default ReadableProduct productToReadableProduct(Product product){
        ReadableProduct readableProduct = new ReadableProduct();
        readableProduct.setId(product.getUuid().toString());
        readableProduct.setActive(product.isStatus());
        readableProduct.setBarcode(product.getBarcode());
        readableProduct.setCategoryName(product.getCategory().getName());
        readableProduct.setName(product.getName());
        readableProduct.setPhotoUrl("http://localhost:8080/photos/"+product.getPhotoUrl());
        readableProduct.setTax(product.getTax());
        readableProduct.setProductSpecifies(product.getProductSpecifies().stream()
                .map(ProductMapper.INSTANCE::productSpecifyToReadableProductSpecify).collect(Collectors.toList()));
        return readableProduct;
    }

    default ReadableProductSpecify productSpecifyToReadableProductSpecify(ProductSpecify productSpecify){
        ReadableProductSpecify readableProductSpecify = new ReadableProductSpecify();
        readableProductSpecify.setId(productSpecify.getUuid().toString());
        readableProductSpecify.setContents(productSpecify.getContents());
        readableProductSpecify.setQuantity(productSpecify.getQuantity());
        readableProductSpecify.setRecommendedRetailPrice(productSpecify.getRecommendedRetailPrice());
        readableProductSpecify.setTotalPrice(productSpecify.getTotalPrice());
        readableProductSpecify.setUnitPrice(productSpecify.getUnitPrice());
        readableProductSpecify.setUnitType(productSpecify.getUnitType());
        readableProductSpecify.setProductName(productSpecify.getProduct().getName());
        return readableProductSpecify;
    }
}
