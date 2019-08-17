package com.marketing.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO implements Serializable {

    @NotBlank
    private String name;

    private String photoUrl;

    @NotNull
    private boolean subCategory;

    private Long parentId;


}
