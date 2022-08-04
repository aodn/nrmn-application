package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.filter.Filter;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import org.hibernate.Criteria;
import org.hibernate.mapping.Join;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * The purpose of this class is to generate select conditions based on incoming filter value, usually it is post
 * from the AGgrid
 *
 * Due to tight couple with the database structure, this class cannot be generalized
 */

public class SurveyFilterCondition {

    public static Specification<Survey> createSpecification(Filter[] filters) {
        SurveyFilterCondition condition = new SurveyFilterCondition();

        if(filters == null || filters.length == 0) {
            // Return null means select all
            return null;
        }
        else {
            List<Specification<Survey>> specifications = new ArrayList<>();

            Arrays.stream(filters).forEach(filter -> {
                // Income filter name not always match the db field name, hence we need a map
                switch (filter.getFieldName()) {
                    case "surveysId": {
                        specifications.add(
                                filter.isCompositeCondition() ?
                                        null :
                                        condition.getSimpleFieldSpecification("surveyId", filter.getValue(), filter.getOperation()));
                        break;
                    }
                }
            });

            // Join all condition with and
            Specification<Survey> resultCondition = null;

            for(int i = 0; i < specifications.size(); i++) {
                resultCondition = resultCondition == null ? specifications.get(i) : resultCondition.and(specifications.get(i));
            }

            return resultCondition;
        }
    }

//    protected Specification<Survey> getJoinFieldSpecification() {
//    }
//
    protected Specification<Survey> getSimpleFieldSpecification(String field, String value, String operation) {

        return ((root, query, criteriaBuilder) -> {
            // Single condition
            switch(operation) {

                case "startsWith": {
                    return criteriaBuilder.like(root.get(field).as(String.class), value + "%");
                }
                case "endsWith": {
                    return criteriaBuilder.like(root.get(field).as(String.class), "%" + value);
                }
                case "contains": {
                    return criteriaBuilder.like(root.get(field).as(String.class), "%" + value + "%");
                }
                case "notContains" : {
                    return criteriaBuilder.notLike(root.get(field).as(String.class), "%" + value + "%");
                }
                case "equals" : {
                    return criteriaBuilder.equal(root.get(field).as(String.class), value);
                }
                case "notEqual" : {
                    return criteriaBuilder.notEqual(root.get(field).as(String.class), value);
                }
                case "blank" : {
                    return criteriaBuilder.or(
                            criteriaBuilder.equal(criteriaBuilder.trim(root.get(field).as(String.class)), ""),
                            criteriaBuilder.isNull(root.get(field)));
                }
                case "notBlank" : {
                    return criteriaBuilder.notEqual(criteriaBuilder.trim(root.get(field).as(String.class)), "");
                }
                default: {
                    return null;
                }
            }
        });
    }
}
