package com.marketing.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperPagination<T> implements Serializable {
    private List<T> values;

    private String key;

    private int pageNumber;

    private int previousPage;

    private int nextPage;

    private int totalPage;

    private long totalElements;

    private int elementCountOfPage;

    private boolean last;

    private boolean first;
}
