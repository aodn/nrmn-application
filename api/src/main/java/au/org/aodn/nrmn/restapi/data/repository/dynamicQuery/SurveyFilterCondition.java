package au.org.aodn.nrmn.restapi.data.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.data.model.*;
import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * The purpose of this class is to generate select conditions based on incoming filter value, usually it is post
 * from the AGgrid
 *
 * Due to tight couple with the database structure, this class cannot be generalized
 */

public class SurveyFilterCondition extends FilterCondition<SurveyListView> {

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
        },
        METHOD {
            @Override
            public String toString() {
                return "survey.method";
            }
            @Override
            public String getDBFieldName() {
                return "method";
            }
        },
        ECOREGION {
            @Override
            public String toString() {
                return "survey.ecoregion";
            }
            @Override
            public String getDBFieldName() {
                return "ecoregion";
            }
        },
        SURVEY_NUM {
            @Override
            public String toString() {
                return "survey.surveyNum";
            }
            @Override
            public String getDBFieldName() {
                return "surveyNum";
            }
        },
        LATITUDE {
            @Override
            public String toString() {
                return "survey.latitude";
            }
            @Override
            public String getDBFieldName() {
                return "latitude";
            }
        },
        LONGITUDE {
            @Override
            public String toString() {
                return "survey.longitude";
            }
            @Override
            public String getDBFieldName() {
                return "longitude";
            }
        }
    }

    public static Specification<SurveyListView> createSpecification(List<Filter> filters, List<Sorter> sort) {
        SurveyFilterCondition condition = new SurveyFilterCondition();

        if(!(filters == null || filters.size() == 0 || !containsSupportField(filters, SupportedFields.class))) {
            condition.applyFilters(filters);
        }

        if(!(sort == null  || sort.size() == 0 || !containsSupportField(sort, SupportedFields.class))) {
            condition.applySort(sort);
        }

        return condition.build();
    }

    protected SurveyFilterCondition applySort(List<Sorter> sort) {
        sortingSpec = createOrdering(sort);
        return this;
    }

    protected Specification<SurveyListView> createOrdering(List<Sorter> sort) {
        return (root, query, criteriaBuilder) -> {
            List<Order> orders = new ArrayList<>();

            sort.forEach(sortItem -> {
                SupportedFields target = getFieldEnum(sortItem.getFieldName(), SupportedFields.class);
                if(target != null) {
                    switch (target) {
                        case LATITUDE:
                        case METHOD:
                        case ECOREGION:
                        case LONGITUDE:
                        case DIVER_NAME:
                        case PROGRAMS:
                        case LOCATION_NAME:
                        case MPA :
                        case COUNTRY :
                        case SITE_CODE :
                        case SITE_NAME :
                        case HAS_PQs:
                        case SURVEY_DATE :
                        case DEPTH :
                        case SURVEY_ID : {
                            orders.add(getItemOrdering(root, criteriaBuilder, sortItem, SupportedFields.class));
                            break;
                        }
                        default:
                            break;
                    }
                }
            });

            return query.orderBy(orders).getRestriction();
        };
    }

    protected SurveyFilterCondition applyFilters(List<Filter> filters) {

        List<Specification<SurveyListView>> specifications = new ArrayList<>();

        filters.forEach(filter -> {
            // Income filter name not always match the db field name, hence we need a switch
            // plus some field need special handle
            SupportedFields target = getFieldEnum(filter.getFieldName(), SupportedFields.class);
            if(target != null) {

                switch (target) {
                    case ECOREGION:
                    case METHOD:
                    case LATITUDE:
                    case LONGITUDE:
                    case DIVER_NAME:
                    case PROGRAMS:
                    case LOCATION_NAME :
                    case MPA :
                    case COUNTRY :
                    case SITE_CODE :
                    case SITE_NAME :
                    case SURVEY_DATE :
                    case SURVEY_ID :
                    case DEPTH :
                    case HAS_PQs : {
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
                    default:
                        break;
                }
            }
        });

        // Join all condition with and
        for(int i = 0; i < specifications.size(); i++) {
            filtersSpec = filtersSpec == null ? specifications.get(i) : filtersSpec.and(specifications.get(i));
        }

        return this;
    }

    protected Specification<SurveyListView> getSurveyFieldSpecification(final SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target, isAnd, filter1, filter2);
    }

    protected Specification<SurveyListView> getSurveyFieldSpecification(final SupportedFields target, Filter filter) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target, filter.getValue(), filter.getOperation());
    }
}
