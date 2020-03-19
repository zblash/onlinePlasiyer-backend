package com.marketing.web.specifications;

import com.marketing.web.enums.SearchOperations;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchCriteria<X extends Comparable<? super X>> {
    private String key;
    private SearchOperations operation;
    private X value;
    private boolean orPredicate;

}
