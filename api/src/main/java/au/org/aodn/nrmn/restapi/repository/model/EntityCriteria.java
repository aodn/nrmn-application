package au.org.aodn.nrmn.restapi.repository.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EntityCriteria<T> {
  public abstract Optional<T> findByCriteria(String crit);
}
