package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.ProductDTO;
import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper( ProductMapper.class );

    @InheritInverseConfiguration
    Product ProductDTOtoProduct(ProductDTO dto);

    @InheritInverseConfiguration
    ProductSpecify ProductDTOtoProductSpecify(ProductDTO dto);
}
