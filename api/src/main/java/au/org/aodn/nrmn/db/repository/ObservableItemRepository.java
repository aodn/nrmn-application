package au.org.aodn.nrmn.db.repository;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.db.model.ObservableItem;
import au.org.aodn.nrmn.db.repository.model.EntityCriteria;
import au.org.aodn.nrmn.db.repository.projections.ObservableItemRow;
import au.org.aodn.nrmn.db.repository.projections.ObservableItemSuperseded;

public interface ObservableItemRepository extends JpaRepository<ObservableItem, Integer>,
        JpaSpecificationExecutor<ObservableItem>, EntityCriteria<ObservableItem> {

    @Override
    @Query("SELECT o from ObservableItem o WHERE o.observableItemName = :name")
    @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
    List<ObservableItem> findByCriteria(@Param("name") String name);

    @Override
    @RestResource
    Page<ObservableItem> findAll(Pageable pageable);

    @Override
    @RestResource
    <S extends ObservableItem> S save(S s);

    @Override
    @RestResource
    Optional<ObservableItem> findById(Integer integer);

    @Query("SELECT o from ObservableItem o WHERE o.observableItemName IN :speciesNames")
    List<ObservableItem> getAllSpeciesNamesMatching(Collection<String> speciesNames);

    @Query("SELECT o from ObservableItem o WHERE o.observableItemName = :name")
    ObservableItem getWithName(@Param("name") String name);

    @Query(value = "select distinct on (obsitem.observable_item_id) obsitem.observable_item_id as observableItemId, obsitem.observable_item_name as name, obsitem.letter_code as letterCode, obsitem.common_name as commonName "
            + "FROM {h-schema}location_ref loc "
            + "INNER JOIN {h-schema}site_ref site_raw ON site_raw.location_id = loc.location_id "
            + "INNER JOIN {h-schema}ep_site_list site ON site.site_code = site_raw.site_code "
            + "INNER JOIN {h-schema}survey sur ON sur.site_id = site_raw.site_id "
            + "INNER JOIN {h-schema}survey_method surmet ON surmet.survey_id = sur.survey_id "
            + "INNER JOIN {h-schema}observation obs ON obs.survey_method_id = surmet.survey_method_id "
            + "INNER JOIN {h-schema}observable_item_ref obsitem ON obsitem.observable_item_id = obs.observable_item_id "
            + "INNER JOIN {h-schema}methods_species m ON m.observable_item_id = obsitem.observable_item_id "
            + "where m.method_id = :methodId AND site_raw.site_id IN :siteIds AND obsitem.superseded_by IS NULL "
            + "AND (:methodId != 2 OR (obsitem.class NOT IN ('Ophiuroidea', 'Polyplacophora') AND obsitem.family != 'Pyuridae'))", nativeQuery = true)
    List<ObservableItemRow> getAllWithMethodForSites(@Param("methodId") Integer methodId, @Param("siteIds") Collection<Integer> siteIds);

    @Query(value = "SELECT * FROM {h-schema}observable_item_ref oi"
            + " LEFT JOIN {h-schema}lengthweight_ref lw ON (lw.observable_item_id = oi.observable_item_id)"
            + " WHERE (:include_superseded = TRUE OR oi.superseded_by IS NULL) "
            + " AND observable_item_name ILIKE '%' || :search_term || '%' "
            + " ORDER BY lower(observable_item_name) DESC ", countQuery = "SELECT count(*) FROM {h-schema}observable_item_ref oi "
                    + " WHERE (:include_superseded = TRUE OR oi.superseded_by IS NULL) "
                    + " AND observable_item_name ILIKE '%' || :search_term || '%' ", nativeQuery = true)
    Page<ObservableItem> fuzzySearch(Pageable pageable, @Param("search_term") String searchTerm,
            @Param("include_superseded") Boolean includeSuperseded);

    @Query("SELECT oi FROM ObservableItem oi WHERE oi.observableItemName = :search_term")
    List<ObservableItem> exactSearch(@Param("search_term") String searchTerm);

    @Query(value = "select superseded_names as supersededNames, superseded_ids as supersededIds "
            + "FROM {h-schema}observable_item_ref oi " + "LEFT JOIN LATERAL ("
            + "select string_agg(oi_1.observable_item_name, ', ' order by oi_1.observable_item_name) as "
            + "superseded_names, "
            + "string_agg(cast(oi_1.observable_item_id AS varchar ), ', ' order by oi_1.observable_item_name) as "
            + "superseded_ids " + "from {h-schema}observable_item_ref oi_1 "
            + "where oi_1.superseded_by = oi.observable_item_name) as superseded on true WHERE observable_item_id = :id", nativeQuery = true)
    ObservableItemSuperseded findSupersededForId(Integer id);
}
