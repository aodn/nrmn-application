package au.org.aodn.nrmn.restapi.data.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.data.model.DiverListView;
import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.List;

public class DiverFilterCondition extends FilterCondition<DiverListView> {

    public enum SupportedFields implements DBField {
        INITIALS {
            @Override
            public String toString() {
                return "diver.initials";
            }

            @Override
            public String getDBFieldName() {
                return "initials";
            }
        },
        FULL_NAME {
            @Override
            public String toString() {
                return "diver.fullName";
            }

            @Override
            public String getDBFieldName() {
                return "fullName";
            }
        }
    }

    public static Specification<DiverListView> createSpecification(List<Filter> filters, List<Sorter> sort) {
        DiverFilterCondition condition = new DiverFilterCondition();

        if(!(filters == null || filters.size() == 0 || !containsSupportField(filters, DiverFilterCondition.SupportedFields.class))) {
            condition.applyFilters(filters);
        }

        if(!(sort == null  || sort.size() == 0 || !containsSupportField(sort, DiverFilterCondition.SupportedFields.class))) {
            condition.applySort(sort);
        }

        return condition.build();
    }

    protected DiverFilterCondition applySort(List<Sorter> sort) {
        sortingSpec = createOrdering(sort);
        return this;
    }

    protected Specification<DiverListView> createOrdering(List<Sorter> sort) {
        return (root, query, criteriaBuilder) -> {
            List<Order> orders = new ArrayList<>();

            sort.forEach(sortItem -> {
                DiverFilterCondition.SupportedFields target = getFieldEnum(sortItem.getFieldName(), DiverFilterCondition.SupportedFields.class);
                if (target != null) {

                    switch(target) {
                        case INITIALS:
                        case FULL_NAME: {
                            orders.add(getItemOrdering(root, criteriaBuilder, sortItem, DiverFilterCondition.SupportedFields.class));
                            break;
                        }
                        default: { break; }
                    }
                }
            });

            return query.orderBy(orders).getRestriction();
        };
    }

    protected DiverFilterCondition applyFilters(List<Filter> filters) {

        List<Specification<DiverListView>> specifications = new ArrayList<>();

        filters.forEach(filter -> {
            DiverFilterCondition.SupportedFields target = getFieldEnum(filter.getFieldName(), DiverFilterCondition.SupportedFields.class);
            switch (target) {
                case INITIALS:
                case FULL_NAME: {
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

                default: { break; }
            }
        });

        // Join all condition with and
        for(int i = 0; i < specifications.size(); i++) {
            filtersSpec = filtersSpec == null ? specifications.get(i) : filtersSpec.and(specifications.get(i));
        }

        return this;
    }

    protected Specification<DiverListView> getLocationFieldSpecification(final DiverFilterCondition.SupportedFields target, Filter filter) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target, filter.getValue(), filter.getOperation());
    }

    protected Specification<DiverListView> getLocationFieldSpecification(final DiverFilterCondition.SupportedFields target, boolean isAnd, Filter filter1, Filter filter2) {
        return (root, query, criteriaBuilder) ->
                getSimpleFieldSpecification(root, criteriaBuilder, target, isAnd, filter1, filter2);
    }
}
