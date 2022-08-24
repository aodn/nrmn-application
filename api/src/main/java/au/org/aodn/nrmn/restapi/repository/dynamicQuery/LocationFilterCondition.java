package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
import au.org.aodn.nrmn.restapi.model.db.Location;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LocationFilterCondition extends FilterCondition {
    public enum SupportedFields implements DBField {
        LOCATION_NAME {
            @Override
            public String toString() {
                return "location.name";
            }

            @Override
            public String getDBFieldName() {
                return "location_name";
            }
        },
        STATUS {
            @Override
            public String toString() {
                return "location.status";
            }

            @Override
            public String getDBFieldName() {
                return "status";
            }
        },
        COUNTRIES {
            @Override
            public String toString() {
                return "location.countries";
            }

            @Override
            public String getDBFieldName() {
                return "country";
            }
        },
    }

    protected Specification<Location> filtersSpec = null;
    protected Specification<Location> sortingSpec = null;

    public static Specification<Location> createSpecification(List<Filter> filters, List<Sorter> sort) {
        LocationFilterCondition condition = new LocationFilterCondition();

        if(!(filters == null || filters.size() == 0 || !containsSupportField(filters, LocationFilterCondition.SupportedFields.class))) {
            condition.applyFilters(filters);
        }

        if(!(sort == null  || sort.size() == 0 || !containsSupportField(sort, LocationFilterCondition.SupportedFields.class))) {
            //condition.applySort(sort);
        }

        return condition.build();
    }

    protected Specification<Location> build() {
        if(filtersSpec == null) {
            return sortingSpec;
        }
        else {
            return filtersSpec.and(sortingSpec);
        }
    }

    protected LocationFilterCondition applyFilters(List<Filter> filters) {

        List<Specification<Location>> specifications = new ArrayList<>();

        filters.forEach(filter -> {
            SurveyFilterCondition.SupportedFields target = getFieldEnum(filter.getFieldName(), SurveyFilterCondition.SupportedFields.class);
            switch (target) {

                case LOCATION_NAME: {
                    if(filter.isCompositeCondition()) {
                        specifications.add(
                                getLocationFieldSpecification(target,
                                        filter.isAndOperation(),
                                        filter.getConditions().get(0),
                                        filter.getConditions().get(1)));
                    }
                    else {
                        specifications.add(getLocationFieldSpecification(target, filter));
                    }
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

    protected Specification<Location> getLocationFieldSpecification(final SurveyFilterCondition.SupportedFields target, Filter filter) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
    }

    protected Specification<Location> getLocationFieldSpecification(final SurveyFilterCondition.SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
    }

}