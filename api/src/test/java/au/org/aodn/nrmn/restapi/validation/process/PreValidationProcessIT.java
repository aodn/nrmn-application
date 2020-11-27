package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.Program;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@ActiveProfiles("cicd")
class PreValidationProcessIT {

    @Autowired
    RawValidation preProcess;

    @Test
    void inputRespectingFormatShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        job.setIsExtendedSize(false);
        val program = new Program();
        program.setProgramId(1);
        program.setProgramName("RLS");
        job.setProgram(program);
        val stage = new StagedRow();
        stage.setSiteNo("EYR71");
        stage.setSiteName("South East Slade Point");
        stage.setLongitude("154");
        stage.setLatitude("-35");
        stage.setDate("16/11/2020");
        stage.setTime("11:32");
        stage.setDiver("TJR");
        stage.setDepth("7.4");
        stage.setMethod("1");
        stage.setBlock("1");
        stage.setSpecies("Specie 56");
        stage.setBuddy("EVP");
        stage.setVis("1");
        stage.setDirection("NE");
        stage.setPqs("EVP");
        stage.setCode("1");
        stage.setTotal("2");
        stage.setStagedJob(job);
        stage.setMeasureJson(new HashMap<Integer, String>() {{
            put(13, "1");
            put(21, "12");
        }});
        val res = preProcess.preValidated(Collections.singletonList(stage), job);

        assertFalse(res.isEmpty());
        val row = res.get(0);
        assertEquals(row.getBlock(), 1);
        assertEquals(row.getDirection(), Directions.NE);
        assertEquals(row.getDiver().getFullName(), "Tanjona Julien Rafidison");
        assertEquals(row.getSpecies().getAphiaId(), 102);
        assertEquals(row.getMeasureJson().get(13), 1);

    }

}
