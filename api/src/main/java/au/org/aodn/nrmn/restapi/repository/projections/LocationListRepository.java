package au.org.aodn.nrmn.restapi.repository.projections;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
import au.org.aodn.nrmn.restapi.model.db.LocationListView;
import au.org.aodn.nrmn.restapi.repository.dynamicQuery.LocationFilterCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

public interface LocationListRepository extends JpaRepository<LocationListView, Integer>, JpaSpecificationExecutor<LocationListView> {

    @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
    default Page<LocationListView> findAllLocationBy(List<Filter> filters, List<Sorter> sort, Pageable pageable) {
        Specification<LocationListView> spec = LocationFilterCondition.createSpecification(filters, sort);
        return this.findAll(spec, pageable);
    }
}
