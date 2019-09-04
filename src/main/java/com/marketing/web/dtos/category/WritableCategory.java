package com.marketing.web.dtos.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.dtos.DTO;
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
public class WritableCategory extends DTO {

    @NotBlank
    private String name;

    @NotNull
    private boolean subCategory;

    private Long parentId;


}
