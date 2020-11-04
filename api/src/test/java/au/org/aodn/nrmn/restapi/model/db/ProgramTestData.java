package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Program.ProgramBuilder;
import au.org.aodn.nrmn.restapi.repository.ProgramRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramTestData {

    @Autowired
    private ProgramRepository programRepository;

    public Program persistedProgram() {
        val program = defaultBuilder().build();
        programRepository.saveAndFlush(program);
        return program;
    }

    public ProgramBuilder defaultBuilder() {
        return Program.builder()
            .programName("PV")
            .isActive(true);
    }
}
