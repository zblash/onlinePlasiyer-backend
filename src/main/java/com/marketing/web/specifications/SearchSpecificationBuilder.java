package com.marketing.web.specifications;

import com.marketing.web.enums.SearchOperations;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class SearchSpecificationBuilder<T> {

    private List<SearchCriteria> criteriaList;

    public SearchSpecificationBuilder() {
        this.criteriaList = new ArrayList<>();
    }

    public SearchSpecificationBuilder add(String key, SearchOperations operation, Object value, boolean orPredicate) {
        criteriaList.add(new SearchCriteria(key, operation, value, orPredicate));
        return this;
    }

    public Specification<T> build() {
        if (criteriaList.size() == 0) {
            return null;
        }

        Specification<T> result = Specification.where(new SearchSpecification<>(criteriaList.get(0)));

        for (int i = 1; i < criteriaList.size(); i++) {
            result = criteriaList.get(i)
                    .isOrPredicate()
                    ? Specification.where(result)
                    .or(new SearchSpecification<>(criteriaList.get(i)))
                    : Specification.where(result)
                    .and(new SearchSpecification<>(criteriaList.get(i)));
        }
        return result;
    }
}
