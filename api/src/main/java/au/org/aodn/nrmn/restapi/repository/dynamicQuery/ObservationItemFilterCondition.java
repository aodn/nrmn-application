package au.org.aodn.nrmn.restapi.repository.dynamicQuery;


import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
import au.org.aodn.nrmn.restapi.model.db.ObservationItemListView;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.List;

public class ObservationItemFilterCondition extends FilterCondition<ObservationItemListView> {

    public enum SupportedFields implements DBField {
        OBSERVATION_ITEM_ID {
            @Override
            public String toString() {
                return "observation.observableItemId";
            }

            @Override
            public String getDBFieldName() {
                return "observableItemId";
            }
        },
        TYPE_NAME {
            @Override
            public String toString() {
                return "observation.typeName";
            }

            @Override
            public String getDBFieldName() {
                return "typeName";
            }
        },
        NAME {
            @Override
            public String toString() {
                return "observation.name";
            }

            @Override
            public String getDBFieldName() {
                return "name";
            }
        },
        COMMON_NAME {
            @Override
            public String toString() {
                return "observation.commonName";
            }

            @Override
            public String getDBFieldName() {
                return "commonName";
            }
        },
        SUPERSEDED_BY {
            @Override
            public String toString() {
                return "observation.supersededBy";
            }

            @Override
            public String getDBFieldName() {
                return "supersededBy";
            }
        },
        SUPERSEDED_IDS {
            @Override
            public String toString() {
                return "observation.supersededIds";
            }

            @Override
            public String getDBFieldName() {
                return "supersededIds";
            }

            @Override
            public Boolean isRequireSplitString() { return true; }
        },
        SUPERSEDED_NAMES {
            @Override
            public String toString() {
                return "observation.supersededNames";
            }

            @Override
            public String getDBFieldName() {
                return "supersededNames";
            }

            @Override
            public Boolean isRequireSplitString() { return true; }
        },
        PHYLUM {
            @Override
            public String toString() {
                return "observation.phylum";
            }

            @Override
            public String getDBFieldName() {
                return "phylum";
            }
        },
        CLASS {
            @Override
            public String toString() {
                return "observation.class";
            }

            @Override
            public String getDBFieldName() {
                return "className";
            }
        },
        ORDER {
            @Override
            public String toString() {
                return "observation.order";
            }

            @Override
            public String getDBFieldName() {
                return "order";
            }
        },
        FAMILY {
            @Override
            public String toString() {
                return "observation.family";
            }

            @Override
            public String getDBFieldName() {
                return "family";
            }
        },
        GENUS {
            @Override
            public String toString() {
                return "observation.genus";
            }

            @Override
            public String getDBFieldName() {
                return "genus";
            }
        }
    }

    public static Specification<ObservationItemListView> createSpecification(List<Filter> filters, List<Sorter> sort) {
        ObservationItemFilterCondition condition = new ObservationItemFilterCondition();

        if(!(filters == null || filters.size() == 0 || !containsSupportField(filters, ObservationItemFilterCondition.SupportedFields.class))) {
            condition.applyFilters(filters);
        }

        if(!(sort == null  || sort.size() == 0 || !containsSupportField(sort, ObservationItemFilterCondition.SupportedFields.class))) {
            condition.applySort(sort);
        }

        return condition.build();
    }

    protected ObservationItemFilterCondition applySort(List<Sorter> sort) {
        sortingSpec = createOrdering(sort);
        return this;
    }

    protected ObservationItemFilterCondition applyFilters(List<Filter> filters) {

        List<Specification<ObservationItemListView>> specifications = new ArrayList<>();

        filters.forEach(filter -> {
            ObservationItemFilterCondition.SupportedFields target = getFieldEnum(filter.getFieldName(), ObservationItemFilterCondition.SupportedFields.class);
            switch (target) {
                case NAME:
                case CLASS:
                case GENUS:
                case ORDER:
                case FAMILY:
                case PHYLUM:
                case TYPE_NAME:
                case COMMON_NAME:
                case SUPERSEDED_BY:
                case SUPERSEDED_IDS:
                case SUPERSEDED_NAMES:
                case OBSERVATION_ITEM_ID: {
                    if(filter.isCompositeCondition()) {
                        specifications.add(
                                getObservationItemsFieldSpecification(target,
                                        filter.isAndOperation(),
                                        filter.getConditions().get(0),
                                        filter.getConditions().get(1)));
                    }
                    else {
                        specifications.add(getObservationItemsFieldSpecification(target, filter));
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

    protected Specification<ObservationItemListView> createOrdering(List<Sorter> sort) {
        return (root, query, criteriaBuilder) -> {
            List<Order> orders = new ArrayList<>();

            sort.forEach(sortItem -> {
                ObservationItemFilterCondition.SupportedFields target = getFieldEnum(sortItem.getFieldName(), ObservationItemFilterCondition.SupportedFields.class);
                if (target != null) {

                    switch(target) {
                        case NAME:
                        case CLASS:
                        case GENUS:
                        case ORDER:
                        case FAMILY:
                        case PHYLUM:
                        case TYPE_NAME:
                        case COMMON_NAME:
                        case SUPERSEDED_BY:
                        case SUPERSEDED_IDS:
                        case SUPERSEDED_NAMES:
                        case OBSERVATION_ITEM_ID: {
                            orders.add(getItemOrdering(root, criteriaBuilder, sortItem, ObservationItemFilterCondition.SupportedFields.class));
                            break;
                        }
                        default: { break; }
                    }
                }
            });

            return query.orderBy(orders).getRestriction();
        };
    }

    protected Specification<ObservationItemListView> getObservationItemsFieldSpecification(final ObservationItemFilterCondition.SupportedFields target, Filter filter) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target, filter.getValue(), filter.getOperation());
    }

    protected Specification<ObservationItemListView> getObservationItemsFieldSpecification(final ObservationItemFilterCondition.SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target, isAnd, filter1, filter2);
    }
}
