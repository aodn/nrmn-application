package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.filter.Filter;
import au.org.aodn.nrmn.restapi.model.db.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * The purpose of this class is to generate select conditions based on incoming filter value, usually it is post
 * from the AGgrid
 *
 * Due to tight couple with the database structure, this class cannot be generalized
 */

public class SurveyFilterCondition extends FilterCondition {

    public enum SupportedFilters implements DBField {
        PROGRAMS {
            @Override
            public String toString() {
                return "survey.programName";
            }
            @Override
            public String getDBFieldName() {
                return "programName";
            }
        },
        LOCATION_NAME {
            @Override
            public String toString() {
                return "survey.locationName";
            }
            @Override
            public String getDBFieldName() {
                return "locationName";
            }
        },
        MPA {
            @Override
            public String toString() {
                return "survey.mpa";
            }
            @Override
            public String getDBFieldName() {
                return "mpa";
            }
        },
        COUNTRY {
            @Override
            public String toString() {
                return "survey.country";
            }
            @Override
            public String getDBFieldName() {
                return "country";
            }
        },
        SITE_CODE {
            @Override
            public String toString() {
                return "survey.siteCode";
            }
            @Override
            public String getDBFieldName() {
                return "siteCode";
            }
        },
        SITE_NAME {
            @Override
            public String toString() {
                return "survey.siteName";
            }
            @Override
            public String getDBFieldName() {
                return "siteName";
            }
        },
        SURVEY_ID {
            @Override
            public String toString() {
                return "survey.surveyId";
            }
            @Override
            public String getDBFieldName() {
                return "surveyId";
            }
        },
        SURVEY_DATE {
            @Override
            public String toString() {
                return "survey.surveyDate";
            }
            @Override
            public String getDBFieldName() {
                return "surveyDate";
            }
        },
        DEPTH {
            @Override
            public String toString() {
                return "survey.depth";
            }
            @Override
            public String getDBFieldName() {
                return "depth";
            }
        },
        HAS_PQs {
            @Override
            public String toString() {
                return "survey.hasPQs";
            }
            @Override
            public String getDBFieldName() {
                return "pqCatalogued";
            }
        };
    }

    public static Specification<Survey> createSpecification(List<Filter> filters) {
        SurveyFilterCondition condition = new SurveyFilterCondition();

        if(filters == null || filters.size() == 0 || !containsSupportField(filters, SupportedFilters.class)) {
            // Return null means select all
            return null;
        }
        else {
            List<Specification<Survey>> specifications = new ArrayList<>();

            filters.forEach(filter -> {
                // Income filter name not always match the db field name, hence we need a switch
                // plus some field need special handle
                SupportedFilters target = getFieldEnum(filter.getFieldName(), SupportedFilters.class);
                if(target != null) {

                    switch (target) {

                        case PROGRAMS: {
                            specifications.add(
                                    filter.isCompositeCondition() ?
                                            null :
                                            condition.getJoinProgramFieldSpecification(target.getDBFieldName(), filter.getValue(), filter.getOperation()));
                            break;
                        }

                        case LOCATION_NAME : {
                            specifications.add(
                                    filter.isCompositeCondition() ?
                                            null :
                                            condition.getJoinLocationFieldSpecification(target.getDBFieldName(), filter.getValue(), filter.getOperation()));
                            break;
                        }

                        case MPA :
                        case COUNTRY :
                        case SITE_CODE :
                        case SITE_NAME : {
                            specifications.add(
                                    filter.isCompositeCondition() ?
                                            null :
                                            condition.getJoinSiteFieldSpecification(target.getDBFieldName(), filter.getValue(), filter.getOperation()));
                            break;
                        }
                        case SURVEY_DATE :
                        case SURVEY_ID : {
                            specifications.add(
                                    filter.isCompositeCondition() ?
                                            null :
                                            condition.getSimpleFieldSpecification(target.getDBFieldName(), filter.getValue(), filter.getOperation()));
                            break;
                        }
                        case DEPTH : {
                            // Special handle, please refer to SurveyRowCacheable, logic make sense?
                            if(filter.isCompositeCondition()) {

                            }
                            else {
                                String[] i = filter.getValue().split("\\.");

                                specifications.add(condition.getSimpleFieldSpecification(target.getDBFieldName(), i[0], filter.getOperation()));

                                if (i.length > 1) {
                                    // We have something after dot
                                    specifications.add(condition.getSimpleFieldSpecification("surveyNum", i[1], filter.getOperation()));
                                }
                            }
                            break;
                        }
                        case HAS_PQs : {
                            // True if not equals null, so we need to rewrite the query

                            if(filter.isCompositeCondition()) {

                            }
                            else {
                                boolean positive = filter.getValue().toLowerCase().matches("^(t|tr|tru|true)");
                                boolean negative = filter.getValue().toLowerCase().matches("^(f|fa|fal|fals|false)");

                                if(positive) {
                                    specifications.add(condition.getSimpleFieldSpecification(target.getDBFieldName(), null, "notBlank"));
                                }
                                else if(negative) {
                                    specifications.add(condition.getSimpleFieldSpecification(target.getDBFieldName(), null, "blank"));
                                }
                                else {
                                    // A string that will never match if user type something else
                                    specifications.add(condition.getSimpleFieldSpecification(target.getDBFieldName(), "-", "equals"));
                                }
                            }
                            break;
                        }
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

    protected Specification<Survey> getJoinLocationFieldSpecification(String field, String value, String operation) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Site> site = root.join("site", JoinType.INNER);
            Join<Site, Location> location = site.join("location", JoinType.INNER);
            return getSimpleFieldSpecification(site, location, criteriaBuilder, field, value, operation);
        });
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

}
