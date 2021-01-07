package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.Site;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@Tag(name="observable items")
public interface ObservableItemRepository extends JpaRepository<ObservableItem, Integer>,
    JpaSpecificationExecutor<ObservableItem> {

    @Override
    @RestResource
    Page<ObservableItem> findAll(Pageable pageable);

    @Override
    @RestResource
    <S extends ObservableItem> S save(S s);

    @Override
    @RestResource
    Optional<ObservableItem> findById(Integer integer);


    @Query("SELECT DISTINCT oi FROM ObservableItem oi " +
            "LEFT JOIN Observation o ON (o.observableItem = oi) " +
            "WHERE oi.letterCode IS NOT NULL " +
            "AND o.surveyMethod.survey.site IN :sites")
    List<ObservableItem> getAllM3ObservableItems(Collection<Site> sites);
}
