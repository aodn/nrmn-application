package au.org.aodn.nrmn.restapi.service.validation;

import java.util.Collection;

import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRowPutDto;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.validation.process.ValidationResultSet;

@Service
public class NullValidationService {

    public Collection<ValidationError> validate(CorrectionRowPutDto row) {

        ValidationResultSet results = new ValidationResultSet();

        // Verify that reference data has not changed
        
        // Get the first observation ID

        // Verify that observations are not changed

        return results.getAll();
    }
}
