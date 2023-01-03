package au.org.aodn.nrmn.restapi.enums;

import java.time.LocalDate;
import java.util.Arrays;

import au.org.aodn.nrmn.restapi.data.model.Program;

public enum ProgramValidation {
    NONE(null),
    RLS(LocalDate.parse("2006-01-01")),
    ATRC(LocalDate.parse("1991-01-01"));

    LocalDate minDate;

    ProgramValidation(LocalDate minDate) {
        this.minDate = minDate;
    }

    public static ProgramValidation fromProgram(Program program) {
        var programName = program.getProgramName().toUpperCase();
        return Arrays.asList("ATRC", "FRDC").contains(programName) ? ProgramValidation.ATRC
        : Arrays.asList("RLS", "PARKS VIC").contains(programName) ? ProgramValidation.RLS
                : ProgramValidation.NONE;
    }

    public LocalDate getMinDate() {return minDate;}
}
