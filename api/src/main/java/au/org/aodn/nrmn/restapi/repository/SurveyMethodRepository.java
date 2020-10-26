package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.SurveyMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyMethodRepository extends JpaRepository<SurveyMethod, Integer>,
 JpaSpecificationExecutor<SurveyMethod> {

}
