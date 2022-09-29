package au.org.aodn.nrmn.restapi.enums;

import java.util.Arrays;

import au.org.aodn.nrmn.restapi.data.model.Program;

public enum ProgramValidation {
    NONE, RLS, ATRC;
    
    public static ProgramValidation fromProgram(Program program) {
        var programName = program.getProgramName().toUpperCase();
        return Arrays.asList("ATRC", "FRDC").contains(programName) ? ProgramValidation.ATRC
        : Arrays.asList("RLS", "PARKS VIC").contains(programName) ? ProgramValidation.RLS
                : ProgramValidation.NONE;
    }
}
