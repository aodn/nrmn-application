package au.org.aodn.nrmn.restapi.repository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.model.db.SpeciesWithAttributes;
import au.org.aodn.nrmn.restapi.repository.projections.SpeciesWithAttributesCsvRow;

@Repository
public interface SpeciesWithAttributesRepository
        extends JpaRepository<SpeciesWithAttributes, Integer>, JpaSpecificationExecutor<SpeciesWithAttributes> {

    default List<SpeciesWithAttributesCsvRow> findAllById(Iterable<Integer> ids,
            HashMap<Integer, String> letterCodeMap) {

        return findAllById(ids).stream()
                .map(s -> SpeciesWithAttributesCsvRow.builder().letterCode(letterCodeMap.get(s.getId()))
                        .speciesName(s.getSpeciesName()).commonName(s.getCommonName())
                        .isInvertSized(s.getIsInvertSized()).l5(s.getL5()).l95(s.getL95()).lMax(s.getLMax()).build())
                .collect(Collectors.toList());
    }
}
