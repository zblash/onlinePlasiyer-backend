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
public class WrapperReadableProduct extends DTO {

    private List<ReadableProduct> products;

    private String key;

    private int pageNumber;

    private int previousPage;

    private int nextPage;

    private int totalPage;

    private int totalElements;

    private int numberOfElements;

    private boolean last;

    private boolean first;
}
