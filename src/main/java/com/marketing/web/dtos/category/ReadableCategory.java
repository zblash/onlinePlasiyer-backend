package com.marketing.web.dtos.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marketing.web.dtos.DTO;
import com.marketing.web.models.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCategory extends DTO {

    private String id;

    private String name;

    private String photoUrl;

    private boolean subCategory;

    private Long parentId;
}
