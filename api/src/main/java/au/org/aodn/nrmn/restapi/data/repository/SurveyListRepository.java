package au.org.aodn.nrmn.restapi.data.repository;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
import au.org.aodn.nrmn.restapi.data.model.SurveyListView;
import au.org.aodn.nrmn.restapi.data.repository.dynamicQuery.SurveyFilterCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;

import java.util.List;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

public interface SurveyListRepository extends JpaRepository<SurveyListView, Integer>, JpaSpecificationExecutor<SurveyListView> {

    @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
    default Page<SurveyListView> findAllProjectedBy(List<Filter> filters, List<Sorter> sort, Pageable pageable) {

        Specification<SurveyListView> spec = SurveyFilterCondition.createSpecification(filters, sort);

        return this.findAll(spec, pageable);
    }

}
