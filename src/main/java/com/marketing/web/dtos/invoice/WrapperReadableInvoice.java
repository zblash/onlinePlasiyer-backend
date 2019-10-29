package com.marketing.web.dtos.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperReadableInvoice implements Serializable {

    private List<ReadableInvoice> values;

    private String key;

    private int pageNumber;

    private int previousPage;

    private int nextPage;

    private int totalPage;

    private long totalElements;

    private int numberOfElements;

    private boolean last;

    private boolean first;
}

