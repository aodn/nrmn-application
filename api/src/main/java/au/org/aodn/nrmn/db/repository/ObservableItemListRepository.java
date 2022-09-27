package au.org.aodn.nrmn.db.repository;

import au.org.aodn.nrmn.db.model.ObservationItemListView;
import au.org.aodn.nrmn.db.repository.dynamicQuery.ObservationItemFilterCondition;
import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

public interface ObservableItemListRepository extends JpaRepository<ObservationItemListView, Integer>, JpaSpecificationExecutor<ObservationItemListView> {

    @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
    default Page<ObservationItemListView> findAllObservationItemBy(List<Filter> filters, List<Sorter> sort, Pageable pageable) {
        Specification<ObservationItemListView> spec = ObservationItemFilterCondition.createSpecification(filters, sort);
        return this.findAll(spec, pageable);
    }
}
