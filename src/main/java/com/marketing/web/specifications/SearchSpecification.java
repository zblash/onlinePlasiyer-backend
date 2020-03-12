package com.marketing.web.specifications;

import com.marketing.web.enums.SearchOperations;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

@Data
@AllArgsConstructor
public class SearchSpecification<T, Y extends Comparable<? super Y>> implements Specification<T> {

    private SearchCriteria<Y> criteria;

    SearchSpecification(String key, SearchOperations operation, Y value, boolean orPredicate){
        criteria = new SearchCriteria<>(key, operation, value, orPredicate);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (SearchOperations.GREATER_THAN.equals(criteria.getOperation())) {
            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()), (Y) criteria.getValue());
        } else if (SearchOperations.LESS_THAN.equals(criteria.getOperation())) {
            return criteriaBuilder.lessThanOrEqualTo(
                    root.get(criteria.getKey()), (Y) criteria.getValue());
        } else if (SearchOperations.EQUAL.equals(criteria.getOperation())) {
            return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
        } else if (SearchOperations.LIKE.equals(criteria.getOperation()) &&
                root.get(criteria.getKey()).getJavaType() == String.class) {
            return criteriaBuilder.like(
                    root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
        }
        return null;
    }

}
