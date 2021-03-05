package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

@RepositoryRestResource
@Tag(name="observable items")
public interface ObservableItemRepository extends JpaRepository<ObservableItem, Integer>,
    JpaSpecificationExecutor<ObservableItem>, EntityCriteria<ObservableItem> {

    @Override
    @Query("SELECT o from ObservableItem o WHERE o.observableItemName = :name")
    @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
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
}
