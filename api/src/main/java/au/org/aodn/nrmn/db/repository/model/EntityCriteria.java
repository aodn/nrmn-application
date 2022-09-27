package au.org.aodn.nrmn.db.repository.model;

import java.util.List;

public interface EntityCriteria<T> {
  List<T> findByCriteria(String crit);
}
