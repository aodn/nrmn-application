package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.filter.Filter;
import au.org.aodn.nrmn.restapi.model.db.Program;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

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
                // Income filter name not always match the db field name, hence we need a switch
                // plus some field need special handle
                switch (filter.getFieldName()) {

                    case "programName" : {
                        specifications.add(
                                filter.isCompositeCondition() ?
                                        null :
                                        condition.getJoinProgramFieldSpecification(filter.getFieldName(), filter.getValue(), filter.getOperation()));
                        break;
                    }

                    case "siteCode" :
                    case "siteName" : {
                        specifications.add(
                                filter.isCompositeCondition() ?
                                        null :
                                        condition.getJoinSiteFieldSpecification(filter.getFieldName(), filter.getValue(), filter.getOperation()));
                        break;
                    }
                    case "surveysId": {
                        specifications.add(
                                filter.isCompositeCondition() ?
                                        null :
                                        condition.getSimpleFieldSpecification("surveyId", filter.getValue(), filter.getOperation()));
                        break;
                    }
                    case "depth" : {
                        // Special handle, please refer to SurveyRowCacheable, logic make sense?
                        if(filter.isCompositeCondition()) {

                        }
                        else {
                            String[] i = filter.getValue().split("\\.");

                            specifications.add(condition.getSimpleFieldSpecification("depth", i[0], filter.getOperation()));

                            if (i.length > 1) {
                                // We have something after dot
                                specifications.add(condition.getSimpleFieldSpecification("surveyNum", i[1], filter.getOperation()));
                            }
                        }
                        break;
                    }
                    case "hasPQs": {
                        // True if not equals null, so we need to rewrite the query

                        if(filter.isCompositeCondition()) {

                        }
                        else {
                            boolean positive = filter.getValue().toLowerCase().matches("^(t|tr|tru|true)");
                            boolean negative = filter.getValue().toLowerCase().matches("^(f|fa|fal|fals|false)");

                            if(positive) {
                                specifications.add(condition.getSimpleFieldSpecification("pqCatalogued", null, "notBlank"));
                            }
                            else if(negative) {
                                specifications.add(condition.getSimpleFieldSpecification("pqCatalogued", null, "blank"));
                            }
                            else {
                                // A string that will never match if user type something else
                                specifications.add(condition.getSimpleFieldSpecification("pqCatalogued", "-", "equals"));
                            }
                        }
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

    protected Specification<Survey> getJoinSiteFieldSpecification(String field, String value, String operation) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Site> site = root.join("site", JoinType.INNER);
            return getSimpleFieldSpecification(root, site, criteriaBuilder, field, value, operation);
        });
    }

    protected Specification<Survey> getJoinProgramFieldSpecification(String field, String value, String operation) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Program> site = root.join("program", JoinType.INNER);
            return getSimpleFieldSpecification(root, site, criteriaBuilder, field, value, operation);
        });
    }

    protected Specification<Survey> getSimpleFieldSpecification(String field, String value, String operation) {
        return ((root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, null, criteriaBuilder, field, value, operation));
    }

    protected Predicate getSimpleFieldSpecification(
            From<?, ?> root, From<?,?> table, CriteriaBuilder criteriaBuilder, String field, String value, String operation) {

        From<?,?> r = table == null ? root : table;

        // Single condition
        switch(operation) {

            case "startsWith": {
                return criteriaBuilder.like(criteriaBuilder.lower(r.get(field).as(String.class)), value.toLowerCase() + "%");
            }
            case "endsWith": {
                return criteriaBuilder.like(criteriaBuilder.lower(r.get(field).as(String.class)), "%" + value.toLowerCase());
            }
            case "contains": {
                return criteriaBuilder.like(criteriaBuilder.lower(r.get(field).as(String.class)), "%" + value.toLowerCase() + "%");
            }
            case "notContains" : {
                return criteriaBuilder.notLike(criteriaBuilder.lower(r.get(field).as(String.class)), "%" + value.toLowerCase() + "%");
            }
            case "equals" : {
                return criteriaBuilder.equal(criteriaBuilder.lower(r.get(field).as(String.class)), value.toLowerCase());
            }
            case "notEqual" : {
                return criteriaBuilder.notEqual(criteriaBuilder.lower(r.get(field).as(String.class)), value.toLowerCase());
            }
            case "blank" : {
                return criteriaBuilder.or(
                        criteriaBuilder.equal(criteriaBuilder.trim(r.get(field).as(String.class)), ""),
                        criteriaBuilder.isNull(r.get(field)));
            }
            case "notBlank" : {
                return criteriaBuilder.notEqual(criteriaBuilder.trim(r.get(field).as(String.class)), "");
            }
            default: {
                return null;
            }
        }
    }
}
