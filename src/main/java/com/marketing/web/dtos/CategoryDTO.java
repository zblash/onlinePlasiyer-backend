package com.marketing.web.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO extends DTO {

    @NotBlank
    private String name;

    @JsonIgnore
    private String photoUrl;

    @NotNull
    private boolean subCategory;

    private Long parentId;


}
