package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.category.ReadableCategory;
import com.marketing.web.dtos.category.WritableCategory;
import com.marketing.web.models.Category;

public final class CategoryMapper {

    public static Category writableCategorytoCategory(WritableCategory writableCategory) {
        if (writableCategory == null) {
            return null;
        } else {
            Category category = new Category();
            category.setSubCategory(writableCategory.isSubCategory());
            category.setName(writableCategory.getName());
            if (writableCategory.getCommission() != null) {
                category.setCommission(writableCategory.getCommission());
            }
            return category;
        }
    }

    public static ReadableCategory categoryToReadableCategory(Category category) {
        if (category == null) {
            return null;
        } else {
            ReadableCategory readableCategory = new ReadableCategory();
            readableCategory.setId(category.getId().toString());
            readableCategory.setName(category.getName());
            readableCategory.setPhotoUrl(category.getPhotoUrl());
            readableCategory.setSubCategory(category.isSubCategory());
            readableCategory.setCommission(category.getCommission());
            if (category.isSubCategory()) {
                readableCategory.setParentId(category.getParent().getId().toString());
                readableCategory.setSubCategoryCount(0);
            } else {
                readableCategory.setSubCategoryCount(category.getChilds() != null ? category.getChilds().size() : 0);
            }
            return readableCategory;
        }
    }
}
