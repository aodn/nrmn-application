package au.org.aodn.nrmn.restapi.repository;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.model.db.SpeciesWithAttributes;

@Repository
public interface SpeciesWithAttributesRepository extends JpaRepository<SpeciesWithAttributes, Integer>, JpaSpecificationExecutor<SpeciesWithAttributes> {
    
    default List<SpeciesWithAttributes> findAllById(Iterable<Integer> ids, HashMap<Integer, String> letterCodeMap) {
        List<SpeciesWithAttributes> result = findAllById(ids);
        result.forEach(s -> s.setLetterCode(letterCodeMap.get(s.getId())));
        return result;
    }
}
