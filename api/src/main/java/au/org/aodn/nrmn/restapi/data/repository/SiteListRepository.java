package au.org.aodn.nrmn.restapi.data.repository;

import au.org.aodn.nrmn.restapi.data.model.SiteListView;
import au.org.aodn.nrmn.restapi.data.repository.dynamicQuery.SiteFilterCondition;
import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;


public interface SiteListRepository extends JpaRepository<SiteListView, Integer>, JpaSpecificationExecutor<SiteListView> {

    default Page<SiteListView> findSiteBy(List<Filter> filters, List<Sorter> sort, Pageable pageable) {
        Specification<SiteListView> spec = SiteFilterCondition.createSpecification(filters, sort);
        return this.findAll(spec, pageable);
    }
}
