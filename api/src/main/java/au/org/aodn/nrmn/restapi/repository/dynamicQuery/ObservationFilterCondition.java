package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.filter.Filter;
import au.org.aodn.nrmn.restapi.model.db.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.List;

public class ObservationFilterCondition extends FilterCondition {

    public enum SupportedFilters implements DBField {
        DIVER_NAME {
            @Override
            public String toString() {
                return "observation.diverName";
            }
            @Override
            public String getDBFieldName() {
                return "fullName";
            }
        },
        SURVEY_ID {
            @Override
            public String toString() {
                return "observation.surveyId";
            }
            @Override
            public String getDBFieldName() {
                return "surveyId";
            }
        };
    }

    public static Specification<Observation> createSpecification(List<Filter> filters) {

        ObservationFilterCondition condition = new ObservationFilterCondition();

        if(filters == null || filters.size() == 0 || !containsSupportField(filters, SupportedFilters.class)) {
            // Return null means select all
            return null;
        }
        else {
            List<Specification<Observation>> specifications = new ArrayList<>();

            filters.forEach(filter -> {

                SupportedFilters target = getFieldEnum(filter.getFieldName(), SupportedFilters.class);
                if(target != null) {
                    switch (target) {
                        case DIVER_NAME : {
                            if(filter.isCompositeCondition()) {
                                specifications.add(
                                        condition.getJoinDiverFieldSpecification(
                                                target,
                                                filter.isAndOperation(),
                                                filter.getConditions().get(0),
                                                filter.getConditions().get(1)));
                            }
                            else {
                                specifications.add(condition.getJoinDiverFieldSpecification(target, filter));
                            }
                            break;
                        }
                        case SURVEY_ID: {
                            if(filter.isCompositeCondition()) {
                                specifications.add(
                                        condition.getJoinSurveyFieldSpecification(
                                                target,
                                                filter.isAndOperation(),
                                                filter.getConditions().get(0),
                                                filter.getConditions().get(1)));
                            }
                            else {
                                specifications.add(condition.getJoinSurveyFieldSpecification(target, filter));
                            }
                            break;
                        }
                    }}
            });

            // Join all condition with and
            Specification<Observation> resultCondition = null;

            for(int i = 0; i < specifications.size(); i++) {
                resultCondition = resultCondition == null ? specifications.get(i) : resultCondition.and(specifications.get(i));
            }

            return resultCondition;
        }
    }

    protected Specification<Observation> getJoinSurveyFieldSpecification(SupportedFilters target, boolean isAnd, Filter filter1, Filter filter2) {
        return ((root, query, criteriaBuilder) -> {
            Join<Observation, SurveyMethodEntity> surveyMethodEntityJoin = root.join("surveyMethod", JoinType.INNER);
            Join<SurveyMethodEntity, Survey> surveyJoin = surveyMethodEntityJoin.join("survey", JoinType.INNER);

            return getSimpleFieldSpecification(surveyJoin, criteriaBuilder, target.getDBFieldName() , isAnd, filter1, filter2);
        });
    }

    protected Specification<Observation> getJoinSurveyFieldSpecification(SupportedFilters target, Filter filter) {
        return ((root, query, criteriaBuilder) -> {
            Join<Observation, SurveyMethodEntity> surveyMethodEntityJoin = root.join("surveyMethod", JoinType.INNER);
            Join<SurveyMethodEntity, Survey> surveyJoin = surveyMethodEntityJoin.join("survey", JoinType.INNER);

            return getSimpleFieldSpecification(surveyJoin, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
        });
    }

    protected Specification<Observation> getJoinDiverFieldSpecification(SupportedFilters target, boolean isAnd, Filter filter1, Filter filter2) {
        return ((root, query, criteriaBuilder) -> {
            Join<Observation, Diver> diver = root.join("diver", JoinType.INNER);

            return getSimpleFieldSpecification(diver, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
        });
    }

    protected Specification<Observation> getJoinDiverFieldSpecification(SupportedFilters target, Filter filter) {
        return ((root, query, criteriaBuilder) -> {
            Join<Observation, Diver> diver = root.join("diver", JoinType.INNER);
            return getSimpleFieldSpecification(diver, criteriaBuilder, target.getDBFieldName(), filter.getValue(), filter.getOperation());
        });
    }
}
