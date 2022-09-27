package au.org.aodn.nrmn.restapi.data.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.data.model.SiteListView;
import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.List;

public class SiteFilterCondition extends FilterCondition<SiteListView> {

    public enum SupportedFields implements DBField {
        SITE_CODE {
            @Override
            public String toString() {
                return "site.siteCode";
            }

            @Override
            public String getDBFieldName() {
                return "siteCode";
            }
        },
        SITE_NAME {
            @Override
            public String toString() {
                return "site.siteName";
            }

            @Override
            public String getDBFieldName() {
                return "siteName";
            }
        },
        LOCATION_NAME {
            @Override
            public String toString() {
                return "site.locationName";
            }

            @Override
            public String getDBFieldName() {
                return "locationName";
            }
        },
        STATE {
            @Override
            public String toString() {
                return "site.state";
            }

            @Override
            public String getDBFieldName() {
                return "state";
            }
        },
        COUNTRY {
            @Override
            public String toString() {
                return "site.country";
            }

            @Override
            public String getDBFieldName() {
                return "country";
            }
        },
        LATITUDE {
            @Override
            public String toString() {
                return "site.latitude";
            }

            @Override
            public String getDBFieldName() {
                return "latitude";
            }
        },
        LONGITUDE {
            @Override
            public String toString() {
                return "site.longitude";
            }

            @Override
            public String getDBFieldName() {
                return "longitude";
            }
        },
        IS_ACTIVE {
            @Override
            public String toString() {
                return "site.isActive";
            }

            @Override
            public String getDBFieldName() {
                return "isActive";
            }
        }
    }

    public static Specification<SiteListView> createSpecification(List<Filter> filters, List<Sorter> sort) {
        SiteFilterCondition condition = new SiteFilterCondition();

        if(!(filters == null || filters.size() == 0 || !containsSupportField(filters, SiteFilterCondition.SupportedFields.class))) {
            condition.applyFilters(filters);
        }

        if(!(sort == null  || sort.size() == 0 || !containsSupportField(sort, SiteFilterCondition.SupportedFields.class))) {
            condition.applySort(sort);
        }

        return condition.build();
    }

    protected SiteFilterCondition applySort(List<Sorter> sort) {
        sortingSpec = createOrdering(sort);
        return this;
    }

    protected Specification<SiteListView> createOrdering(List<Sorter> sort) {
        return (root, query, criteriaBuilder) -> {
            List<Order> orders = new ArrayList<>();

            sort.forEach(sortItem -> {
                SiteFilterCondition.SupportedFields target = getFieldEnum(sortItem.getFieldName(), SiteFilterCondition.SupportedFields.class);
                if (target != null) {

                    switch(target) {
                        case SITE_CODE:
                        case STATE:
                        case COUNTRY:
                        case LATITUDE:
                        case IS_ACTIVE:
                        case LONGITUDE:
                        case LOCATION_NAME:
                        case SITE_NAME: {
                            orders.add(getItemOrdering(root, criteriaBuilder, sortItem, SiteFilterCondition.SupportedFields.class));
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

    protected SiteFilterCondition applyFilters(List<Filter> filters) {

        List<Specification<SiteListView>> specifications = new ArrayList<>();

        filters.forEach(filter -> {
            SiteFilterCondition.SupportedFields target = getFieldEnum(filter.getFieldName(), SiteFilterCondition.SupportedFields.class);
            switch (target) {
                case SITE_CODE:
                case STATE:
                case COUNTRY:
                case LATITUDE:
                case IS_ACTIVE:
                case LONGITUDE:
                case LOCATION_NAME:
                case SITE_NAME: {
                    if(filter.isCompositeCondition()) {
                        specifications.add(
                                getSiteFieldSpecification(target,
                                        filter.isAndOperation(),
                                        filter.getConditions().get(0),
                                        filter.getConditions().get(1)));
                    }
                    else {
                        specifications.add(getSiteFieldSpecification(target, filter));
                    }
                    break;
                }

                default: { break; }
            }
        });

        // Join all condition with and
        for(int i = 0; i < specifications.size(); i++) {
            filtersSpec = filtersSpec == null ? specifications.get(i) : filtersSpec.and(specifications.get(i));
        }

        return this;
    }

    protected Specification<SiteListView> getSiteFieldSpecification(final SiteFilterCondition.SupportedFields target, Filter filter) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target, filter.getValue(), filter.getOperation());
    }

    protected Specification<SiteListView> getSiteFieldSpecification(final SiteFilterCondition.SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target, isAnd, filter1, filter2);
    }
}
