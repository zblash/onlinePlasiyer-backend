package com.marketing.web.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableProduct implements Serializable {

    private String id;

    private String barcode;

    private String categoryName;

    private String name;

    private String photoUrl;

    private boolean isActive;

    private double tax;

}
