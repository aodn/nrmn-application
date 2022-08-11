package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
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

    public enum SupportedFields implements DBField {
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
        },
        // We do not filter operation here, for reason please read ObservationFilterCondition
        DIVER_NAME {
            @Override
            public String toString() {
                return "survey.diverName";
            }
            @Override
            public String getDBFieldName() {
                return "fullName";
            }
        }
    }

    protected Specification<Survey> filtersSpec = null;
    protected Specification<Survey> sortingSpec = null;

    public static Specification<Survey> createSpecification(List<Filter> filters, List<Sorter> sort) {
        SurveyFilterCondition condition = new SurveyFilterCondition();

        if(!(filters == null || filters.size() == 0 || !containsSupportField(filters, SupportedFields.class))) {
            condition.applyFilters(filters);
        }

        if(!(sort == null  || sort.size() == 0 || !containsSupportField(sort, SupportedFields.class))) {
            condition.applySort(sort);
        }

        return condition.build();
    }

    protected Specification<Survey> build() {
        if(filtersSpec == null) {
            return sortingSpec;
        }
        else {
            return filtersSpec.and(sortingSpec);
        }
    }

    protected SurveyFilterCondition applySort(List<Sorter> sort) {
        sortingSpec = createOrdering(sort);
        return this;
    }

    protected SurveyFilterCondition applyFilters(List<Filter> filters) {

        List<Specification<Survey>> specifications = new ArrayList<>();

        filters.forEach(filter -> {
            // Income filter name not always match the db field name, hence we need a switch
            // plus some field need special handle
            SupportedFields target = getFieldEnum(filter.getFieldName(), SupportedFields.class);
            if(target != null) {

                switch (target) {

                    case PROGRAMS: {
                        if(filter.isCompositeCondition()) {
                            specifications.add(
                                    getJoinProgramFieldSpecification(
                                            target,
                                            filter.isAndOperation(),
                                            filter.getConditions().get(0),
                                            filter.getConditions().get(1)));
                        }
                        else {
                            specifications.add(getJoinProgramFieldSpecification(target, filter));
                        }
                        break;
                    }

                    case LOCATION_NAME : {
                        if(filter.isCompositeCondition()) {
                            specifications.add(
                                    getJoinLocationFieldSpecification(
                                            target,
                                            filter.isAndOperation(),
                                            filter.getConditions().get(0),
                                            filter.getConditions().get(1)));
                        }
                        else {
                        }
                        specifications.add(getJoinLocationFieldSpecification(target, filter));
                        break;
                    }

                    case MPA :
                    case COUNTRY :
                    case SITE_CODE :
                    case SITE_NAME : {
                        if(filter.isCompositeCondition()) {
                            specifications.add(
                                    getJoinSiteFieldSpecification(
                                            target,
                                            filter.isAndOperation(),
                                            filter.getConditions().get(0),
                                            filter.getConditions().get(1)));
                        }
                        else {
                            specifications.add(getJoinSiteFieldSpecification(target, filter));
                        }
                        break;
                    }
                    case SURVEY_DATE :
                    case SURVEY_ID : {
                        if(filter.isCompositeCondition()) {
                            specifications.add(
                                    getSurveyFieldSpecification(target,
                                            filter.isAndOperation(),
                                            filter.getConditions().get(0),
                                            filter.getConditions().get(1)));
                        }
                        else {
                            specifications.add(getSurveyFieldSpecification(target, filter));
                        }
                        break;
                    }
                    case DEPTH : {
                        // Special handle, please refer to SurveyRowCacheable, logic make sense?
                        if(filter.isCompositeCondition()) {
                            specifications.add(
                                    getDepthSpecification(
                                            target,
                                            filter.isAndOperation(),
                                            filter.getConditions().get(0),
                                            filter.getConditions().get(1)));
                        }
                        else {
                            specifications.add(getDepthSpecification(target, filter));
                        }
                        break;
                    }
                    case HAS_PQs : {
                        if(filter.isCompositeCondition()) {
                            specifications.add(
                                    getHasPQSpecification(
                                            target,
                                            filter.isAndOperation(),
                                            filter.getConditions().get(0),
                                            filter.getConditions().get(1)));
                        }
                        else {
                            specifications.add(getHasPQSpecification(target, filter));
                        }
                        break;
                    }
                }
            }
        });

        // Join all condition with and
        for(int i = 0; i < specifications.size(); i++) {
            filtersSpec = filtersSpec == null ? specifications.get(i) : filtersSpec.and(specifications.get(i));
        }

        return this;
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

    protected Specification<Survey> getHasPQSpecification(final SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) -> {
            Filter f1 = getHasPqFilterBy(filter1);
            Filter f2 = getHasPqFilterBy(filter2);

            return getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), isAnd, f1, f2);
        };
    }

    protected Specification<Survey> getHasPQSpecification(final SupportedFields target, final Filter filter) {
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

    protected Specification<Survey> getDepthSpecification(final SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
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

    protected Specification<Survey> getDepthSpecification(final SupportedFields target, final Filter filter) {
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

    protected Specification<Survey> getJoinLocationFieldSpecification(final SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Site> site = root.join("site", JoinType.INNER);
            Join<Site, Location> location = site.join("location", JoinType.INNER);
            return getSimpleFieldSpecification(location, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
        });
    }

    protected Specification<Survey> getJoinLocationFieldSpecification(final SupportedFields target, Filter filter) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Site> site = root.join("site", JoinType.INNER);
            Join<Site, Location> location = site.join("location", JoinType.INNER);
            return getSimpleFieldSpecification(location, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
        });
    }

    protected Specification<Survey> getJoinSiteFieldSpecification(final SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Site> site = root.join("site", JoinType.INNER);

            return getSimpleFieldSpecification(site, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
        });
    }

    protected Specification<Survey> getJoinSiteFieldSpecification(final SupportedFields target, Filter filter) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Site> site = root.join("site", JoinType.INNER);
            return getSimpleFieldSpecification(site, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
        });
    }

    protected Specification<Survey> getJoinProgramFieldSpecification(final SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Program> prog = root.join("program", JoinType.INNER);
            return getSimpleFieldSpecification(prog, criteriaBuilder,  target.getDBFieldName(), isAnd, filter1, filter2);
        });
    }

    protected Specification<Survey> getJoinProgramFieldSpecification(final SupportedFields target, Filter filter) {
        return ((root, query, criteriaBuilder) -> {
            Join<Survey, Program> prog = root.join("program", JoinType.INNER);
            return getSimpleFieldSpecification(prog, criteriaBuilder,  target.getDBFieldName(), filter.getValue(), filter.getOperation());
        });
    }

    protected Specification<Survey> getSurveyFieldSpecification(final SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
    }

    protected Specification<Survey> getSurveyFieldSpecification(final SupportedFields target, Filter filter) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
    }

    protected Specification<Survey> createOrdering(List<Sorter> sort) {
        return (root, query, criteriaBuilder) -> {
            List<Order> orders = new ArrayList<>();

            // Depends on user sort, we may or may not need it
            Predicate diverNameJoin = null;

            sort.forEach(sortItem -> {
                SupportedFields target = getFieldEnum(sortItem.getFieldName(), SupportedFields.class);
                if(target != null) {
                    switch (target) {
                        case DIVER_NAME: {
                            orders.add(getDiverNameJoin(root, query, criteriaBuilder, sortItem.isAsc()));
                            break;
                        }
                        case PROGRAMS: {
                            Join<Survey, Program> prog = root.join("program", JoinType.INNER);
                            orders.add(getItemOrdering(prog, criteriaBuilder, sortItem));
                            break;
                        }
                        case LOCATION_NAME: {
                            Join<Survey, Site> site = root.join("site", JoinType.INNER);
                            Join<Site, Location> location = site.join("location", JoinType.INNER);
                            orders.add(getItemOrdering(location, criteriaBuilder, sortItem));
                            break;
                        }
                        case MPA :
                        case COUNTRY :
                        case SITE_CODE :
                        case SITE_NAME : {
                            Join<Survey, Site> site = root.join("site", JoinType.INNER);
                            orders.add(getItemOrdering(site, criteriaBuilder, sortItem));
                            break;
                        }
                        case HAS_PQs:
                        case SURVEY_DATE :
                        case SURVEY_ID : {
                            orders.add(getItemOrdering(root, criteriaBuilder, sortItem));
                            break;
                        }
                        case DEPTH : {
                            // Need to concat two fields
                            orders.add(getItemOrderingContact(root, criteriaBuilder, SupportedFields.DEPTH.getDBFieldName(), "surveyNum", sortItem.isAsc()));
                            break;
                        }
                    }
                }
            });

            query.orderBy(orders);
            return diverNameJoin != null ? diverNameJoin : criteriaBuilder.conjunction();
        };
    }

    protected Order getItemOrdering(From<?,?> from, CriteriaBuilder criteriaBuilder, Sorter sort) {
        Expression<Survey> e = from.get(SurveyFilterCondition.getFieldEnum(sort.getFieldName(), SupportedFields.class).getDBFieldName());
        return (sort.isAsc()  ? criteriaBuilder.asc(e) : criteriaBuilder.desc(e));
    }

    protected Order getItemOrderingContact(From<?,?> from, CriteriaBuilder criteriaBuilder, String f1, String f2, boolean isAsc) {
        // It is field1 . field2, we do this because the field on screen is a concat of two fields in db
        Expression<String> c = criteriaBuilder.concat(
                criteriaBuilder.concat(from.get(f1), "."),
                from.get(f2));

        return (isAsc ? criteriaBuilder.asc(c) : criteriaBuilder.desc(c));
    }

    protected Order getDiverNameJoin(From<?,?> root, CriteriaQuery query, CriteriaBuilder criteriaBuilder, boolean isAsc) {

        if(query.getResultType().equals(Survey.class)) {
            Join<Survey, SurveyMethodEntity> surveyMethodEntityRoot = root.join("surveyMethods", JoinType.LEFT);
            Join<SurveyMethodEntity, Observation> observationRoot = surveyMethodEntityRoot.join("observations", JoinType.LEFT);
            Join<Observation, Diver> diverJoin = observationRoot.join("diver", JoinType.INNER);

            // DB specific call !!
            Expression name = diverJoin.get(SupportedFields.DIVER_NAME.getDBFieldName());
            Expression<String> diverRowConcat = criteriaBuilder
                    .function(PGDialect.STRING_AGG_DISTINCT_ASC, String.class, name, name);

            query.groupBy(root);

            return (isAsc ? criteriaBuilder.asc(diverRowConcat) : criteriaBuilder.desc(diverRowConcat));
        }
        else {
            // That means we are not dealing with Survey object but the count() query from jpa, in this case
            // we should not join the table which cause it report incorrect count on survey table, also it is
            // much efficient query by return null. The other join will not have impact due to only this
            // table with 1 to many
            return null;
        }
    }
}
