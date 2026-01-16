package au.org.aodn.nrmn.restapi.enums;

import java.time.LocalDate;
import java.util.Arrays;

import au.org.aodn.nrmn.restapi.data.model.Program;

public enum ProgramValidation {
    NONE(null),
    RLS(LocalDate.parse("2006-01-01")),
    ATRC(LocalDate.parse("1991-01-01")),
    ;

    LocalDate minDate;

    ProgramValidation(LocalDate minDate) {
        this.minDate = minDate;
    }

    // Return validation rules based on the program name. ATRC and FRDC share the same rules,
    // RLS, PARKS VIC and RRH share the same rules.
    public static ProgramValidation fromProgram(Program program) {
        var programName = program.getProgramName().toUpperCase();
        if (Arrays.asList("ATRC", "FRDC").contains(programName)) {
            return ProgramValidation.ATRC;
        }
        if (Arrays.asList("RLS", "PARKS VIC", "RRH").contains(programName)) {
            return ProgramValidation.RLS;
        }
        return ProgramValidation.NONE;
    }

    public LocalDate getMinDate() {return minDate;}
}
