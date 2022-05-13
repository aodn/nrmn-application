package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.Program;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import au.org.aodn.nrmn.restapi.util.TimeUtils;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@WithTestData
class StagedRowFormattedMappingIT {

    @Autowired
    ValidationProcess validationProcess;

    @Autowired
    ObservableItemRepository observableItemRepository;

    @Test
    void inputRespectingFormatShouldSucceed() {

        StagedJob job = new StagedJob();
        job.setId(1L);
        job.setIsExtendedSize(false);
        Program program = new Program();
        program.setProgramId(1);
        program.setProgramName("RLS");
        job.setProgram(program);
        StagedRow row = new StagedRow();
        row.setSiteCode("EYR71");
        row.setSiteName("South East Slade Point");
        row.setLongitude("154");
        row.setLatitude("-35");
        row.setDate("16/11/20");
        row.setTime("11:32");
        row.setDiver("JEP");
        row.setDepth("7.4");
        row.setMethod("1");
        row.setBlock("1");
        row.setInverts("0");
        row.setSpecies("Species 56");
        row.setBuddy("EVP");
        row.setDirection("NE");
        row.setPqs("EVP");
        row.setCode("1");
        row.setVis("10.0");
        row.setTotal("2");
        row.setStagedJob(job);
        row.setMeasureJson(new HashMap<Integer, String>() {
            {
                put(1, "1");
                put(2, "12");
                put(13, "1");
            }
        });

        Collection<ObservableItem> species = observableItemRepository
                .getAllSpeciesNamesMatching(Arrays.asList(row.getSpecies()));
        Collection<StagedRowFormatted> validatedRows = validationProcess.formatRowsWithSpecies(Arrays.asList(row),
                species);

        assertEquals(1, validatedRows.size());
        StagedRowFormatted formattedRow = (StagedRowFormatted) validatedRows.toArray()[0];
        assertEquals("EYR71", formattedRow.getSite().getSiteCode());
        assertEquals("South East Slade Point", formattedRow.getSite().getSiteName());
        assertEquals(154, formattedRow.getLongitude());
        assertEquals(-35, formattedRow.getLatitude());
        assertEquals(LocalDate.parse("16/11/2020", DateTimeFormatter.ofPattern("d/M/yyyy")), formattedRow.getDate());
        assertEquals(TimeUtils.parseTime("11:32"), formattedRow.getTime());
        assertEquals("JEP", formattedRow.getDiver().getInitials());
        assertEquals("Juan Espanol Pagina", formattedRow.getDiver().getFullName());
        assertEquals(1, formattedRow.getBlock());
        assertEquals(Directions.NE, formattedRow.getDirection());
        assertEquals(102, formattedRow.getSpecies().get().getAphiaId());
        assertEquals(1, formattedRow.getMeasureJson().get(13));
        assertEquals(Optional.of(10.0), formattedRow.getVis());
        assertEquals(4, formattedRow.getSurveyNum());
        assertEquals(7, formattedRow.getDepth());
    }
}
