package com.marketing.web.specifications;

import com.marketing.web.enums.SearchOperations;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
public class SearchSpecification<T> implements Specification<T> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (SearchOperations.GREATER_THAN.equals(criteria.getOperation())) {
            if (root.get(criteria.getKey()).getJavaType() == LocalDate.class) {
                return criteriaBuilder.greaterThanOrEqualTo(
                        root.get(criteria.getKey()), (LocalDate) criteria.getValue());
            }
            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (SearchOperations.LESS_THAN.equals(criteria.getOperation())) {
            if (root.get(criteria.getKey()).getJavaType() == LocalDate.class) {
                return criteriaBuilder.lessThanOrEqualTo(
                        root.get(criteria.getKey()), (LocalDate) criteria.getValue());
            }
            return criteriaBuilder.lessThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
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
