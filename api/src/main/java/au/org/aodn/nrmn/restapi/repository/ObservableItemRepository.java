package au.org.aodn.nrmn.restapi.repository;

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

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import au.org.aodn.nrmn.restapi.repository.projections.ObservableItemRow;
import au.org.aodn.nrmn.restapi.repository.projections.ObservableItemSuperseded;

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

    @Query(value = "select distinct o.observable_item_id as observableItemId, o.common_name as name, o.superseded_by as supersededBy from {h-schema}observable_item_ref o " +
                "left join {h-schema}methods_species ms ON ms.observable_item_id = o.observable_item_id " +
                "left join {h-schema}observation b on o.observable_item_id = b.observable_item_id " +
                "left join {h-schema}survey_method m on b.survey_method_id = m.method_id " +
                "left join {h-schema}survey s on s.survey_id = m.survey_id " +
                "left join {h-schema}site_ref i on s.site_id = i.site_id " +
                "where ms.method_id = :methodId AND i.site_id IN :siteIds " +
                "AND (ms.method_id != 2 OR (o.class_name NOT IN ('Ophiuroidea', 'Polyplacophora') AND o.family NOT 'Pyuridae'))", nativeQuery = true)
    List<ObservableItemRow> getAllWithMethodForSites(@Param("methodId") Integer methodId, @Param("siteIds") Collection<Integer> siteIds);

    @Query(value = "SELECT * FROM {h-schema}observable_item_ref oi"
            + " LEFT JOIN {h-schema}lengthweight_ref lw ON (lw.observable_item_id = oi.observable_item_id)"
            + " WHERE (:include_superseded = TRUE OR oi.superseded_by IS NULL) "
            + " AND observable_item_name ILIKE :search_term || '%' "
            + " ORDER BY lower(observable_item_name) DESC ", countQuery = "SELECT count(*) FROM {h-schema}observable_item_ref oi "
                    + " WHERE (:include_superseded = TRUE OR oi.superseded_by IS NULL) "
                    + " AND observable_item_name ILIKE :search_term || '%' ", nativeQuery = true)
    Page<ObservableItem> fuzzySearch(Pageable pageable, @Param("search_term") String searchTerm,
            @Param("include_superseded") Boolean includeSuperseded);

    @Query(value = "select observable_item_id as observableItemId, obs_item_type_name as typeName, observable_item_name as name, "
            + "common_name as commonName, phylum, class as className, \"order\", family, genus, superseded_by as "
            + "supersededBy, supersededNames, supersededIds " + "FROM {h-schema}observable_item_ref oi "
            + "LEFT JOIN {h-schema}obs_item_type_ref oitr ON oitr.obs_item_type_id = oi.obs_item_type_id "
            + "LEFT JOIN LATERAL ("
            + "select string_agg(oi_1.observable_item_name, ', ' order by oi_1.observable_item_name) as "
            + "supersededNames, "
            + "string_agg(cast(oi_1.observable_item_id AS varchar ), ', ' order by oi_1.observable_item_name) as "
            + "supersededIds " + "from {h-schema}observable_item_ref oi_1 "
            + "where oi_1.superseded_by = oi.observable_item_name) as superseded on true", countQuery = "SELECT count(*) FROM {h-schema}observable_item_ref", nativeQuery = true)
    List<ObservableItemRow> findAllProjectedBy();

    @Query(value = "select superseded_names as supersededNames, superseded_ids as supersededIds "
            + "FROM {h-schema}observable_item_ref oi " + "LEFT JOIN LATERAL ("
            + "select string_agg(oi_1.observable_item_name, ', ' order by oi_1.observable_item_name) as "
            + "superseded_names, "
            + "string_agg(cast(oi_1.observable_item_id AS varchar ), ', ' order by oi_1.observable_item_name) as "
            + "superseded_ids " + "from {h-schema}observable_item_ref oi_1 "
            + "where oi_1.superseded_by = oi.observable_item_name) as superseded on true WHERE observable_item_id = :id", nativeQuery = true)
    ObservableItemSuperseded findSupersededForId(Integer id);
}
