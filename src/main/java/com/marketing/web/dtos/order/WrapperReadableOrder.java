package com.marketing.web.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperReadableOrder implements Serializable {

    private List<ReadableOrder> orders;

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
