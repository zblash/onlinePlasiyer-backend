package com.marketing.web.dtos.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableCategory implements Serializable {

    @NotBlank
    private String name;

    @NotNull
    private boolean subCategory;

    private String parentId;


}
