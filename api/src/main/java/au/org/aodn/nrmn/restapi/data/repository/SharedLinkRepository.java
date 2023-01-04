package au.org.aodn.nrmn.restapi.data.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.data.model.SharedLink;

@Repository
public interface SharedLinkRepository
        extends JpaRepository<SharedLink, Long>, JpaSpecificationExecutor<SharedLink> {

    @Query("SELECT DISTINCT s.linkType FROM SharedLink s")
    Collection<String> findAllDistinctLinkTypes();
}
