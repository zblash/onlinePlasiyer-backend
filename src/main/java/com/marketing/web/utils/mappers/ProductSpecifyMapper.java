package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.ProductSpecifyDTO;
import com.marketing.web.models.ProductSpecify;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductSpecifyMapper {

    ProductSpecifyMapper INSTANCE = Mappers.getMapper( ProductSpecifyMapper.class );

    @InheritInverseConfiguration
    ProductSpecify dtoToProductSpecify(ProductSpecifyDTO productSpecifyDTO);
}
