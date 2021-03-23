package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.ObservableItemRow;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@RepositoryRestResource
@Tag(name = "observable items")
public interface ObservableItemRowRepository extends JpaRepository<ObservableItemRow, Integer>,
 JpaSpecificationExecutor<ObservableItemRow> {

    @Query(value =
    "select *" +
        "FROM nrmn.observable_item_ref oi " +
        "LEFT JOIN LATERAL (" +
        "select string_agg(oi_1.observable_item_name, ', ' order by oi_1.observable_item_name) as superseded_names, " +
        "string_agg(oi_1.observable_item_id::text, ', ' order by oi_1.observable_item_name) as superseded_ids " +
        "from nrmn.observable_item_ref oi_1 " +
        "where oi_1.superseded_by = oi.observable_item_name) as superseded on true",
    countQuery = "SELECT count(*) FROM {h-schema}observable_item_ref",
    nativeQuery = true)
    Page<ObservableItemRow> findAll(Pageable pageable);
    
}
