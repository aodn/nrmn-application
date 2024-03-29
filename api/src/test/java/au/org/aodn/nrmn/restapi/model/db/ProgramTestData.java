package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.Program;
import au.org.aodn.nrmn.restapi.data.model.Program.ProgramBuilder;
import au.org.aodn.nrmn.restapi.data.repository.ProgramRepository;

@Component
public class ProgramTestData {

    @Autowired
    private ProgramRepository programRepository;

    private int programNo = 0;

    public Program persistedProgram() {
        Program program = defaultBuilder().build();
        return persistedProgram(program);
    }

    public Program persistedProgram(Program program) {
        programRepository.saveAndFlush(program);
        return program;
    }

    public Program buildWith(int itemNumber) {
        return Program.builder()
                .programName("P" + itemNumber)
                .isActive(true)
                .build();
    }

    public ProgramBuilder defaultBuilder() {
        return Program.builder()
                      .programName("P" + ++programNo)
                      .isActive(true);
    }
}
