package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.category.ReadableCategory;
import com.marketing.web.dtos.category.WritableCategory;
import com.marketing.web.models.Category;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper( CategoryMapper.class );

    default Category writableCategorytoCategory(WritableCategory writableCategory){
        Category category = new Category();
        category.setSubCategory(writableCategory.isSubCategory());
        category.setName(writableCategory.getName());
        return category;
    }

    default ReadableCategory categoryToReadableCategory(Category category){
        ReadableCategory readableCategory = new ReadableCategory();
        readableCategory.setId(category.getUuid().toString());
        readableCategory.setName(category.getName());
        readableCategory.setPhotoUrl(category.getPhotoUrl());
        readableCategory.setSubCategory(category.isSubCategory());
        if (category.isSubCategory()){
            readableCategory.setParentId(category.getParent().getUuid().toString());
        }
        return readableCategory;
    }
}
