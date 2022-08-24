package au.org.aodn.nrmn.restapi.repository;

import java.util.List;
import java.util.Optional;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
import au.org.aodn.nrmn.restapi.repository.dynamicQuery.LocationFilterCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.repository.projections.LocationExtendedMapping;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.persistence.QueryHint;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

@RepositoryRestResource
@Tag(name = "locations")
public interface LocationRepository extends JpaRepository<Location, Integer>, JpaSpecificationExecutor<Location> {

    @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
    default Page<LocationExtendedMapping> findAllLocationBy(List<Filter> filters, List<Sorter> sort, Pageable pageable) {
        Specification<Location> spec = LocationFilterCondition.createSpecification(filters, sort);
        return this.findAll(spec, pageable).map(t -> new LocationExtendedMapping(t));
    }

    @Override
    @RestResource
    <S extends Location> S save(S s);

    @Override
    @RestResource
    Optional<Location> findById(Integer integer);

//    @Query(value = "SELECT distinct loc.location_id as id, loc.location_name as locationName, " +
//        "CASE loc.is_active WHEN true THEN 'Active' ELSE 'Inactive' END as status, " +
//        "string_agg(DISTINCT sit.country, ', ' ORDER BY sit.country) AS countries, "+
//        "string_agg(DISTINCT sit.state, ', ' ORDER BY sit.state) AS areas, "+
//        "string_agg(DISTINCT sit.site_code, ', ' ORDER BY sit.site_code) AS siteCodes, " +
//        "string_agg(DISTINCT meo.ecoregion, ', ' ORDER BY meo.ecoregion) AS ecoRegions "+
//        "FROM nrmn.location_ref loc " +
//        "LEFT JOIN nrmn.site_ref sit ON loc.location_id = sit.location_id " +
//        "LEFT JOIN nrmn.meow_ecoregions meo ON st_contains(meo.geom, sit.geom) " +
//        "LEFT JOIN nrmn.survey sur ON sur.site_id = sit.site_id " +
//        "GROUP BY loc.location_id, locationName", nativeQuery = true)
//    List<LocationExtendedMapping> getAllWithRegions();
}
