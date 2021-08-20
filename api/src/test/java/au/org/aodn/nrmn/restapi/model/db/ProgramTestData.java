package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Program.ProgramBuilder;
import au.org.aodn.nrmn.restapi.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramTestData {

    @Autowired
    private ProgramRepository programRepository;

    private int programNo = 0;

    public Program persistedProgram() {
        Program program = defaultBuilder().build();
        programRepository.saveAndFlush(program);
        return program;
    }

    public ProgramBuilder defaultBuilder() {
        return Program.builder()
                      .programName("P" + ++programNo)
                      .isActive(true);
    }
}
