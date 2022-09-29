package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.data.model.Method;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;

@ExtendWith(MockitoExtension.class)
class SpeciesSupersededTest {

    @InjectMocks
    ValidationProcess validationProcess;

    @Mock
    DiverRepository diverRepository;

    @Test
    public void supersededSpeciesShouldFail() {
        final Set<Method> methods = new HashSet<Method>();
        methods.add(Method.builder().methodId(1).build());
        StagedRow row = new StagedRow();
        row.setSpecies("THE SPECIES");
        List<ObservableItem> species = Arrays.asList(ObservableItem.builder().observableItemName("THE SPECIES").supersededBy("NEXT SPECIES").methods(methods).build());
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList(), species, Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(p -> p.getMessage().contains("Superseded by")));
    }
}
