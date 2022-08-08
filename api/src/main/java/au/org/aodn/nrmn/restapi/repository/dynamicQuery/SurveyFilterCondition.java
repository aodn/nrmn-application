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
                            if(filter.isCompositeCondition()) {
                                specifications.add(
                                        condition.getJoinProgramFieldSpecification(
                                                target,
                                                filter.isAndOperation(),
                                                filter.getConditions().get(0),
                                                filter.getConditions().get(1)));
                            }
                            else {
                                specifications.add(condition.getJoinProgramFieldSpecification(target, filter));
                            }
                            break;
                        }

                        case LOCATION_NAME : {
                            if(filter.isCompositeCondition()) {
                                specifications.add(
                                        condition.getJoinLocationFieldSpecification(
                                                target,
                                                filter.isAndOperation(),
                                                filter.getConditions().get(0),
                                                filter.getConditions().get(1)));
                            }
                            else {
                            }
                                specifications.add(condition.getJoinLocationFieldSpecification(target, filter));
                            break;
                        }

                        case MPA :
                        case COUNTRY :
                        case SITE_CODE :
                        case SITE_NAME : {
                            if(filter.isCompositeCondition()) {
                                specifications.add(
                                        condition.getJoinSiteFieldSpecification(
                                                target,
                                                filter.isAndOperation(),
                                                filter.getConditions().get(0),
                                                filter.getConditions().get(1)));
                            }
                            else {
                                specifications.add(condition.getJoinSiteFieldSpecification(target, filter));
                            }
                            break;
                        }
                        case SURVEY_DATE :
                        case SURVEY_ID : {
                            if(filter.isCompositeCondition()) {
                                specifications.add(
                                        condition.getSurveyFieldSpecification(target,
                                                filter.isAndOperation(),
                                                filter.getConditions().get(0),
                                                filter.getConditions().get(1)));
                            }
                            else {
                                specifications.add(condition.getSurveyFieldSpecification(target, filter));
                            }
                            break;
                        }
                        case DEPTH : {
                            // Special handle, please refer to SurveyRowCacheable, logic make sense?
                            if(filter.isCompositeCondition()) {
                                specifications.add(
                                        condition.getDepthSpecification(
                                                target,
                                                filter.isAndOperation(),
                                                filter.getConditions().get(0),
                                                filter.getConditions().get(1)));
                            }
                            else {
                                specifications.add(condition.getDepthSpecification(target, filter));
                            }
                            break;
                        }
                        case HAS_PQs : {
                            if(filter.isCompositeCondition()) {
                                specifications.add(
                                        condition.getHasPQSpecification(
                                                target,
                                                filter.isAndOperation(),
                                                filter.getConditions().get(0),
                                                filter.getConditions().get(1)));
                            }
                            else {
                                specifications.add(condition.getHasPQSpecification(target, filter));
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

    protected Filter getHasPqFilterBy(Filter filter) {
        // True = not equals blank, so we need to rewrite the query
        Filter f = new Filter();

        if (filter.getValue().toLowerCase().matches("^(t|tr|tru|true)")) {
            f.setOperation(SurveyFilterCondition.NOT_BLANK);
        }
        else if (filter.getValue().toLowerCase().matches("^(f|fa|fal|fals|false)")) {
            f.setOperation(SurveyFilterCondition.BLANK);
        }
        else {
            // A string that will never match if user type something else
            f.setOperation(SurveyFilterCondition.EQUALS);
            f.setValue("-");
        }
        return f;
    }

    protected Specification<Survey> getHasPQSpecification(final SupportedFilters target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) -> {
            Filter f1 = getHasPqFilterBy(filter1);
            Filter f2 = getHasPqFilterBy(filter2);

            return getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), isAnd, f1, f2);
        };
    }

    protected Specification<Survey> getHasPQSpecification(final SupportedFilters target, final Filter filter) {
        return (root, query, criteriaBuilder) -> {
            Filter f = getHasPqFilterBy(filter);
            return getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), f.getValue(), f.getOperation());
        };
    }

    protected List<Filter> getDepthFilterBy(Filter filter) {
        String[] i = filter.getValue().split("\\.");
        List<Filter> result = new ArrayList<>();
        Filter f1 = new Filter();

        // if value is .xxxx, we assume user do not care what is in front
        f1.setOperation("".equals(i[0].trim()) ? SurveyFilterCondition.NOT_BLANK : filter.getOperation());
        f1.setValue(i[0]);
        result.add(f1);

        if(i.length > 1) {
            // We have something after dot
            Filter f2 = new Filter();
            f2.setOperation(filter.getOperation());
            f2.setValue(i[1]);
            result.add(f2);
        }

        return result;
    }

    protected Specification<Survey> getDepthSpecification(final SupportedFilters target, boolean isAnd, Filter filter1, Filter filter2) {
        return ((root, query, criteriaBuilder) -> {

            Specification<Survey> spec1 = getDepthSpecification(target, filter1);
            Specification<Survey> spec2 = getDepthSpecification(target, filter2);

            if(isAnd) {
                return criteriaBuilder.and(
                        spec1.toPredicate(root, query, criteriaBuilder),
                        spec2.toPredicate(root, query, criteriaBuilder));
            }
            else {
                return criteriaBuilder.or(
                        spec1.toPredicate(root, query, criteriaBuilder),
                        spec2.toPredicate(root, query, criteriaBuilder));
            }
        });
    }

    protected Specification<Survey> getDepthSpecification(final SupportedFilters target, final Filter filter) {
        return ((root, query, criteriaBuilder) -> {
            List<Filter> f = getDepthFilterBy(filter);
            Predicate spec = this.getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), f.get(0).getValue(), f.get(0).getOperation());

            if (f.size() > 1) {
                // We have something after dot
                return criteriaBuilder.and(
                        spec,
                        getSimpleFieldSpecification(root, criteriaBuilder, "surveyNum", f.get(1).getValue(), f.get(1).getOperation()));
            }
            else {
                return spec;
            }
        });
    }

    protected Specification<Survey> getJoinLocationFieldSpecification(final SupportedFilters target, boolean isAnd, Filter filter1, Filter filter2) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Site> site = root.join("site", JoinType.INNER);
            Join<Site, Location> location = site.join("location", JoinType.INNER);
            return getSimpleFieldSpecification(location, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
        });
    }

    protected Specification<Survey> getJoinLocationFieldSpecification(final SupportedFilters target, Filter filter) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Site> site = root.join("site", JoinType.INNER);
            Join<Site, Location> location = site.join("location", JoinType.INNER);
            return getSimpleFieldSpecification(location, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
        });
    }

    protected Specification<Survey> getJoinSiteFieldSpecification(final SupportedFilters target, boolean isAnd, Filter filter1, Filter filter2) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Site> site = root.join("site", JoinType.INNER);

            return getSimpleFieldSpecification(site, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
        });
    }

    protected Specification<Survey> getJoinSiteFieldSpecification(final SupportedFilters target, Filter filter) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Site> site = root.join("site", JoinType.INNER);
            return getSimpleFieldSpecification(site, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
        });
    }

    protected Specification<Survey> getJoinProgramFieldSpecification(final SupportedFilters target, boolean isAnd, Filter filter1, Filter filter2) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Program> site = root.join("program", JoinType.INNER);
            return getSimpleFieldSpecification(site, criteriaBuilder,  target.getDBFieldName(), isAnd, filter1, filter2);
        });
    }

    protected Specification<Survey> getJoinProgramFieldSpecification(final SupportedFilters target, Filter filter) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Program> site = root.join("program", JoinType.INNER);
            return getSimpleFieldSpecification(site, criteriaBuilder,  target.getDBFieldName(), filter.getValue(), filter.getOperation());
        });
    }

    protected Specification<Survey> getSurveyFieldSpecification(final SupportedFilters target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
    }

    protected Specification<Survey> getSurveyFieldSpecification(final SupportedFilters target, Filter filter) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
    }
}
