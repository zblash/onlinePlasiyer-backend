package com.marketing.web.specifications;

import com.marketing.web.enums.SearchOperations;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class SearchSpecificationBuilder<T> {

    private List<SearchSpecification> criteriaList;

    public SearchSpecificationBuilder() {
        this.criteriaList = new ArrayList<>();
    }

    public <X extends Comparable<? super X>> SearchSpecificationBuilder add(String key, SearchOperations operation, X value, boolean orPredicate) {
        criteriaList.add(new SearchSpecification(key, operation, value, orPredicate));
        return this;
    }

    public <X extends Comparable<? super X>> Specification<T> build() {
        if (criteriaList.size() == 0) {
            return null;
        }

        Specification<T> result = Specification.where(criteriaList.get(0));

        for (int i = 1; i < criteriaList.size(); i++) {
            result = criteriaList.get(i).getCriteria()
                    .isOrPredicate()
                    ? Specification.where(result)
                    .or(criteriaList.get(i))
                    : Specification.where(result)
                    .and(criteriaList.get(i));
        }
        return result;
    }
}
