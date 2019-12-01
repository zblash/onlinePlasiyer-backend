package com.marketing.web.dtos.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCategory implements Serializable {

    private String id;

    private String name;

    private String photoUrl;

    private boolean isSubCategory;

    private String parentId;

    private int subCategoryCount;
}
