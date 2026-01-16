package au.org.aodn.nrmn.restapi.enums;

import java.time.LocalDate;
import java.util.Arrays;

import au.org.aodn.nrmn.restapi.data.model.Program;

public enum ProgramValidation {
    NONE(null),
    RLS(LocalDate.parse("2006-01-01")),
    ATRC(LocalDate.parse("1991-01-01")),

    // TODO: confirm date
    RRH(LocalDate.parse("1991-01-01"))
    ;

    LocalDate minDate;

    ProgramValidation(LocalDate minDate) {
        this.minDate = minDate;
    }

    public static ProgramValidation fromProgram(Program program) {
        var programName = program.getProgramName().toUpperCase();
        if (Arrays.asList("ATRC", "FRDC").contains(programName)) {
            return ProgramValidation.ATRC;
        }
        if (Arrays.asList("RLS", "PARKS VIC").contains(programName)) {
            return ProgramValidation.RLS;
        }
        if (Arrays.asList("RRH").contains(programName)) {
            return ProgramValidation.RRH;
        }
        return ProgramValidation.NONE;
    }

    public LocalDate getMinDate() {return minDate;}
}
