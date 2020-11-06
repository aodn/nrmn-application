package au.org.aodn.nrmn.restapi.repository.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EntityCriteria<T> {
  public abstract  List<T> findByCriteria(String crit);
}
