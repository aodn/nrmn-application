package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.model.db.Site;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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
                return "isActive";
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
        SITE_CODE {
            @Override
            public String toString() {
                return "location.areas";
            }

            @Override
            public String getDBFieldName() {
                return "site_code";
            }
        }
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
            // We need to do join to get the relationship even we do not have any search criteria, if we have
            // search criteria, table already join so no need to do it here again
            return getDefaultSpec().and(sortingSpec);
        }
        else {
            return filtersSpec.and(sortingSpec);
        }
    }

    protected LocationFilterCondition applyFilters(List<Filter> filters) {

        List<Specification<Location>> specifications = new ArrayList<>();

        filters.forEach(filter -> {
            LocationFilterCondition.SupportedFields target = getFieldEnum(filter.getFieldName(), LocationFilterCondition.SupportedFields.class);
            switch (target) {
                case SITE_CODE:
                case COUNTRIES:
                case STATUS: {
                    if(filter.isCompositeCondition()) {
                        specifications.add(
                                getJoinSiteSpecification(target,
                                        filter.isAndOperation(),
                                        filter.getConditions().get(0),
                                        filter.getConditions().get(1)));
                    }
                    else {
                        specifications.add(getJoinSiteSpecification(target, filter));
                    }
                    break;
                }
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

    protected Specification<Location> getDefaultSpec() {
        return (root, query, criteriaBuilder) -> {
            createSiteJoin(root, criteriaBuilder);
            return criteriaBuilder.conjunction();
        };
    }

    protected Specification<Location> getLocationFieldSpecification(final LocationFilterCondition.SupportedFields target, Filter filter) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
    }

    protected Specification<Location> getLocationFieldSpecification(final LocationFilterCondition.SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
    }

    protected Specification<Location> getJoinSiteSpecification(final LocationFilterCondition.SupportedFields target, Filter filter) {
        return (root, query, criteriaBuilder) -> {
            Join<Location, Site> joinSiteRoot = createSiteJoin(root, criteriaBuilder);
            // Status field need special handle because in db this is a boolean field, however on screen it is Active / Inactive.
            if(target == SupportedFields.STATUS) {
                return null;
            }
            else {
                return getSimpleFieldSpecification(joinSiteRoot, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
            }
        };
    }

    protected Specification<Location> getJoinSiteSpecification(final LocationFilterCondition.SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) -> {
            Join<Location, Site> joinSiteRoot = createSiteJoin(root, criteriaBuilder);
            return getSimpleFieldSpecification(joinSiteRoot, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
        };
    }

    protected Join<Location, Site> createSiteJoin(From<?, ?> root, CriteriaBuilder criteriaBuilder) {
        Join<Location, Site> joinSiteRoot = root.join("site", JoinType.LEFT);
        return joinSiteRoot;
    }
}