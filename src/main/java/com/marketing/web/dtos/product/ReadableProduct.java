package com.marketing.web.dtos.product;

import com.marketing.web.dtos.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableProduct extends DTO {

    private String id;

    private String barcode;

    private String categoryName;

    private String name;

    private String photoUrl;

    private boolean isActive;

    private double tax;
}
