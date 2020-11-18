package au.org.aodn.nrmn.restapi.repository.model;

import java.util.Optional;

public interface EntityCriteria<T> {
  Optional<T> findByCriteria(String crit);
}
