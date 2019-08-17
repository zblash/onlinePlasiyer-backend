package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.CategoryDTO;
import com.marketing.web.models.Category;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper( CategoryMapper.class );

    @InheritInverseConfiguration
    Category CategoryDTOtoCategory(CategoryDTO dto);

}
