package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.filter.Filter;
import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ObservationFilterCondition extends FilterCondition {

    @Autowired
    protected ObservationRepository observationRepository;

    public enum SupportedFilters implements DBField {
        DIVER_NAME_IN_SURVEY {
            @Override
            public String toString() {
                return "survey.diverName";
            }
            @Override
            public String getDBFieldName() {
                return "fullName";
            }
        },
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
                        case DIVER_NAME_IN_SURVEY: {
                            if(filter.isCompositeCondition()) {
                                specifications.add(
                                        condition.getJoinConcatDiverFieldSpecification(
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
    /**
     * The diver name is present in different way in survey and needs different handle. The diver name are contact
     * to a single string and multiple filter are applied to the concat string
     * @param target
     * @param isAnd
     * @param filter1
     * @param filter2
     * @return
     */
    protected Specification<Observation> getJoinConcatDiverFieldSpecification(SupportedFilters target, boolean isAnd, Filter filter1, Filter filter2) {
        return ((root, query, criteriaBuilder) -> {
            if(isAnd) {
                Subquery<Integer> subquery1 = query.subquery(Integer.class);
                Subquery<Integer> subquery2 = subquery1.subquery(Integer.class);

                // Filter 1
                Root<Observation> r1 = subquery1.from(Observation.class);
                Join<Observation, SurveyMethodEntity> s1Join = r1.join("surveyMethod", JoinType.INNER);
                Join<SurveyMethodEntity, Survey> c1Join = s1Join.join("survey", JoinType.INNER);
                Join<Observation, Diver> d1Join = r1.join("diver", JoinType.INNER);

                // Filter 2
                Root<Observation> r2 = subquery2.from(Observation.class);
                Join<Observation, SurveyMethodEntity> s2Join = r2.join("surveyMethod", JoinType.INNER);
                Join<SurveyMethodEntity, Survey> c2Join = s2Join.join("survey", JoinType.INNER);
                Join<Observation, Diver> d2Join = r2.join("diver", JoinType.INNER);

                subquery1.select(c1Join.get("surveyId"));
                subquery1.distinct(true);
                subquery1.where(getSimpleFieldSpecification(d1Join, criteriaBuilder, target.getDBFieldName(), filter1.getValue(), filter1.getOperation()));

                subquery2.select(c2Join.get("surveyId"));
                subquery2.distinct(true);
                subquery2.where(
                        criteriaBuilder.and(
                                getSimpleFieldSpecification(d2Join, criteriaBuilder, target.getDBFieldName(), filter2.getValue(), filter2.getOperation()),
                                c2Join.get("surveyId").in(subquery1)));

                Join<Observation, SurveyMethodEntity> rootSurveyMethodJoin = root.join("surveyMethod", JoinType.INNER);
                Join<SurveyMethodEntity, Survey> rootSurveyJoin = rootSurveyMethodJoin.join("survey", JoinType.INNER);

                return rootSurveyJoin.get("surveyId").in(subquery2);

            }
            else {
                Join<Observation, Diver> diver = root.join("diver", JoinType.INNER);
                return getSimpleFieldSpecification(diver, criteriaBuilder, target.getDBFieldName(), isAnd, filter1, filter2);
            }
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
