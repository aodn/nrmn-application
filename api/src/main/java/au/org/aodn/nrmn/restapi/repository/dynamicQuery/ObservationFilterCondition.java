package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.filter.Filter;
import au.org.aodn.nrmn.restapi.model.db.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObservationFilterCondition extends FilterCondition {

    enum SupportedFilters implements DBField {
        DIVER_NAME {
            @Override
            public String toString() {
                return "observation.diverName";
            }
            @Override
            public String getDBFieldName() {
                return "fullName";
            }
        };
    }

    public static Specification<Observation> createSpecification(Filter[] filters) {

        ObservationFilterCondition condition = new ObservationFilterCondition();

        if(filters == null || filters.length == 0 || !containsSupportField(filters, SupportedFilters.class)) {
            // Return null means select all
            return null;
        }
        else {
            List<Specification<Observation>> specifications = new ArrayList<>();

            Arrays.stream(filters).forEach(filter -> {
                SupportedFilters target = getFieldEnum(filter.getFieldName(), SupportedFilters.class);
                switch (target) {
                    case DIVER_NAME : {
                        specifications.add(
                                filter.isCompositeCondition() ?
                                        null :
                                        condition.getJoinDiverFieldSpecification(target.getDBFieldName(), filter.getValue(), filter.getOperation()));
                        break;
                    }
                }});

            // Join all condition with and
            Specification<Observation> resultCondition = null;

            for(int i = 0; i < specifications.size(); i++) {
                resultCondition = resultCondition == null ? specifications.get(i) : resultCondition.and(specifications.get(i));
            }

            return resultCondition;
        }
    }

    protected Specification<Observation> getJoinDiverFieldSpecification(String field, String value, String operation) {
        return ((root, query, criteriaBuilder) -> {
            Join<Observation, Diver> diver = root.join("diver", JoinType.INNER);
            return getSimpleFieldSpecification(root, diver, criteriaBuilder, field, value, operation);
        });
    }
}
