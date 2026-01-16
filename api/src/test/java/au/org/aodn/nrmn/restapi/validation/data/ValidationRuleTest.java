package au.org.aodn.nrmn.restapi.validation.data;

import au.org.aodn.nrmn.restapi.data.model.Program;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ValidationRuleTest {

    @Test
    void correctValidationRuleApplied() {
        var rls = new Program(1, "RLS", true);
        var atrc = new Program(2, "ATRC", true);
        var parksVic = new Program(3, "Parks Vic", false);
        var frdc = new Program(4, "FRDC", true);
        var rrh = new Program(5, "RRH", true);

        // ATRC and FRDC share the same rules, and RLS, PARKS VIC and RRH share the same rules
        assertEquals(ProgramValidation.RLS, ProgramValidation.fromProgram(rls));
        assertEquals(ProgramValidation.ATRC, ProgramValidation.fromProgram(atrc));
        assertEquals(ProgramValidation.RLS, ProgramValidation.fromProgram(parksVic));
        assertEquals(ProgramValidation.ATRC, ProgramValidation.fromProgram(frdc));
        assertEquals(ProgramValidation.RLS, ProgramValidation.fromProgram(rrh));

    }
}
