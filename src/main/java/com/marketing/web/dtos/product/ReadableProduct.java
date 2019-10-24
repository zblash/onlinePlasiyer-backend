package com.marketing.web.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableProduct implements Serializable {

    private String id;

    private List<String> barcodeList;

    private String categoryName;

    private String name;

    private String photoUrl;

    private boolean isActive;

    private double tax;

}
