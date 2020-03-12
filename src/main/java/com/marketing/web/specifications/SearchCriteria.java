package com.marketing.web.specifications;

import com.marketing.web.enums.SearchOperations;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchCriteria {
    private String key;
    private SearchOperations operation;
    private Object value;
    private boolean orPredicate;

}
