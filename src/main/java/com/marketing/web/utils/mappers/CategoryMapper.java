package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.category.ReadableCategory;
import com.marketing.web.dtos.category.WritableCategory;
import com.marketing.web.models.Category;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper( CategoryMapper.class );

    @InheritInverseConfiguration
    Category writableCategorytoCategory(WritableCategory writableCategory);

    default ReadableCategory categoryToReadableCategory(Category category){
        ReadableCategory readableCategory = new ReadableCategory();
        readableCategory.setId(category.getUuid().toString());
        readableCategory.setName(category.getName());
        readableCategory.setPhotoUrl("http://localhost:8080/photos/"+category.getPhotoUrl());
        readableCategory.setSubCategory(category.isSubCategory());
        if (category.isSubCategory()){
            readableCategory.setParentId("category_"+category.getParent().getId());
        }
        return readableCategory;
    }
}
