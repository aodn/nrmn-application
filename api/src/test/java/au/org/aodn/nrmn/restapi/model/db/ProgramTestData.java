package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.ProgramRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramTestData {

    @Autowired
    private ProgramRepository programRepository;

    public Program persistedProgram() {
        val program = Program.builder()
            .programName("RLS")
            .isActive(true)
            .build();
        programRepository.saveAndFlush(program);
        return program;
    }
}
