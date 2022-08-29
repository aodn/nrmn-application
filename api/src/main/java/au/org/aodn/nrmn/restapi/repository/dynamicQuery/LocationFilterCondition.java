package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
import au.org.aodn.nrmn.restapi.model.db.LocationListView;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LocationFilterCondition extends FilterCondition {
    public enum SupportedFields implements DBField {
        LOCATION_NAME {
            @Override
            public String toString() {
                return "location.locationName";
            }

            @Override
            public String getDBFieldName() {
                return "locationName";
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
                return "countries";
            }
        },
        SITE_CODE {
            @Override
            public String toString() {
                return "location.areas";
            }

            @Override
            public String getDBFieldName() {
                return "areas";
            }
        },
        ECO_REGIONS {
            @Override
            public String toString() {
                return "location.ecoRegions";
            }

            @Override
            public String getDBFieldName() {
                return "ecoRegions";
            }
        }
    }

    protected Specification<LocationListView> filtersSpec = null;
    protected Specification<LocationListView> sortingSpec = null;

    public static Specification<LocationListView> createSpecification(List<Filter> filters, List<Sorter> sort) {
        LocationFilterCondition condition = new LocationFilterCondition();

        if(!(filters == null || filters.size() == 0 || !containsSupportField(filters, LocationFilterCondition.SupportedFields.class))) {
            condition.applyFilters(filters);
        }

        if(!(sort == null  || sort.size() == 0 || !containsSupportField(sort, LocationFilterCondition.SupportedFields.class))) {
            //condition.applySort(sort);
        }

        return condition.build();
    }

    protected Specification<LocationListView> build() {
        if(filtersSpec == null) {
            // We need to do join to get the relationship even we do not have any search criteria, if we have
            // search criteria, table already join so no need to do it here again
            return sortingSpec;
        }
        else {
            return filtersSpec.and(sortingSpec);
        }
    }

    protected LocationFilterCondition applyFilters(List<Filter> filters) {

        List<Specification<LocationListView>> specifications = new ArrayList<>();

        filters.forEach(filter -> {
            LocationFilterCondition.SupportedFields target = getFieldEnum(filter.getFieldName(), LocationFilterCondition.SupportedFields.class);
            switch (target) {
                case SITE_CODE:
                case COUNTRIES:
                case ECO_REGIONS: filter.setOperation(SPLIT_STRING_CONTAINS);
                case STATUS:
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

                default: {}
            }
        });

        // Join all condition with and
        for(int i = 0; i < specifications.size(); i++) {
            filtersSpec = filtersSpec == null ? specifications.get(i) : filtersSpec.and(specifications.get(i));
        }

        return this;
    }

    protected Specification<LocationListView> getLocationFieldSpecification(final LocationFilterCondition.SupportedFields target, Filter filter) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target, filter.getValue(), filter.getOperation());
    }

    protected Specification<LocationListView> getLocationFieldSpecification(final LocationFilterCondition.SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target, isAnd, filter1, filter2);
    }
}