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
import java.util.Locale;

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
                return "state";
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

    protected Filter translateFilterForStatus(LocationFilterCondition.SupportedFields target, Filter filter) {
        // Status field need special handle because in db this is a boolean field, however on screen it is Active / Inactive string
        if(target == SupportedFields.STATUS) {
            if(filter.getOperation().equals(LocationFilterCondition.CONTAINS) || filter.getOperation().equals(LocationFilterCondition.NOT_CONTAINS)) {
                boolean active = "active".contains(filter.getValue().toLowerCase());
                boolean inactive = "inactive".contains(filter.getValue().toLowerCase());

                if(active && inactive) {
                    // a word that matches both like "act", hence from db point view we do not care the value as long
                    // as it is true or false
                    return new Filter(filter.getFieldName(), "true,false",
                            filter.getOperation().equals(LocationFilterCondition.CONTAINS) ? LocationFilterCondition.IN : LocationFilterCondition.NOT_IN,
                            null);
                }
                else if(active) {
                    return new Filter(filter.getFieldName(), "true",
                            filter.getOperation().equals(LocationFilterCondition.CONTAINS) ? LocationFilterCondition.EQUALS : LocationFilterCondition.NOT_EQUALS,
                            null);
                }
                else if(inactive) {
                    // We have empty and false
                    return new Filter(filter.getFieldName(), "true",
                            filter.getOperation().equals(LocationFilterCondition.CONTAINS) ? LocationFilterCondition.NOT_EQUALS : LocationFilterCondition.EQUALS,
                            null);
                }
                else {
                    // Not ture not false
                    return new Filter(filter.getFieldName(), "true,false",
                            filter.getOperation().equals(LocationFilterCondition.CONTAINS) ? LocationFilterCondition.NOT_IN : LocationFilterCondition.IN,
                            null);
                }
            }
            else if(filter.getOperation().equals(LocationFilterCondition.EQUALS) || filter.getOperation().equals(LocationFilterCondition.NOT_EQUALS)) {
                boolean active = "active".equalsIgnoreCase(filter.getValue());
                boolean inactive = "inactive".equalsIgnoreCase(filter.getValue());

                if(active) {
                    return new Filter(filter.getFieldName(), "true", filter.getOperation(), null);
                }
                else if(inactive) {
                    return new Filter(filter.getFieldName(), "false", filter.getOperation(), null);
                }
                else {
                    // A value that will not match no matter what
                    return new Filter(filter.getFieldName(), "true,false",
                            filter.getOperation().equals(LocationFilterCondition.EQUALS) ? LocationFilterCondition.NOT_IN : LocationFilterCondition.IN
                            , null);
                }
            }
            else if(filter.getOperation().equals(LocationFilterCondition.STARTS_WITH)) {
                boolean active = "active".startsWith(filter.getValue().toLowerCase());
                boolean inactive = "inactive".startsWith(filter.getValue().toLowerCase());

                if(active) {
                    return new Filter(filter.getFieldName(), "true", LocationFilterCondition.EQUALS, null);
                }
                else if(inactive) {
                    return new Filter(filter.getFieldName(), "false", LocationFilterCondition.EQUALS, null);
                }
                else {
                    // A value that will not match no matter what
                    return new Filter(filter.getFieldName(), "true,false", LocationFilterCondition.NOT_IN, null);
                }
            }
            else if(filter.getOperation().equals(LocationFilterCondition.ENDS_WITH)) {
                boolean active = "active".endsWith(filter.getValue().toLowerCase());
                boolean inactive = "inactive".endsWith(filter.getValue().toLowerCase());

                if(active && inactive) {
                    return new Filter(filter.getFieldName(), "true,false", LocationFilterCondition.IN, null);
                }
                else if(active) {
                    return new Filter(filter.getFieldName(), "true", LocationFilterCondition.EQUALS, null);
                }
                else if(inactive) {
                    return new Filter(filter.getFieldName(), "false", LocationFilterCondition.EQUALS, null);
                }
                else {
                    // A value that will not match no matter what
                    return new Filter(filter.getFieldName(), "true,false", LocationFilterCondition.NOT_IN, null);
                }
            }
            else {
                return filter;
            }
        }
        else {
            return filter;
        }
    }

    protected Specification<Location> getJoinSiteSpecification(final LocationFilterCondition.SupportedFields target, Filter filter) {
        return (root, query, criteriaBuilder) -> {
            Join<Location, Site> joinSiteRoot = createSiteJoin(root, criteriaBuilder);
            Filter f = translateFilterForStatus(target, filter);
            return getSimpleFieldSpecification(joinSiteRoot, criteriaBuilder, target.getDBFieldName(), f.getValue(), f.getOperation());
        };
    }

    protected Specification<Location> getJoinSiteSpecification(final LocationFilterCondition.SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) -> {
            Join<Location, Site> joinSiteRoot = createSiteJoin(root, criteriaBuilder);

            Filter f1 = translateFilterForStatus(target, filter1);
            Filter f2 = translateFilterForStatus(target, filter2);

            return getSimpleFieldSpecification(joinSiteRoot, criteriaBuilder, target.getDBFieldName(), isAnd, f1, f2);
        };
    }

    protected Join<Location, Site> createSiteJoin(From<?, ?> root, CriteriaBuilder criteriaBuilder) {
        Join<Location, Site> joinSiteRoot = root.join("site", JoinType.LEFT);
        return joinSiteRoot;
    }
}