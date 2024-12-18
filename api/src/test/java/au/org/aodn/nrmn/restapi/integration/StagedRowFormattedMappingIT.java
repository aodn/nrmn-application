package au.org.aodn.nrmn.restapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.Program;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.enums.Directions;
import au.org.aodn.nrmn.restapi.service.formatting.SpeciesFormattingService;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import au.org.aodn.nrmn.restapi.util.TimeUtils;

import javax.transaction.Transactional;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@Transactional
@WithTestData
class StagedRowFormattedMappingIT {

    @Autowired
    SpeciesFormattingService speciesFormatting;

    @Autowired
    ObservableItemRepository observableItemRepository;

    @Test
    void inputRespectingFormatShouldSucceed() {

        StagedJob job = StagedJob.builder()
                .id(1L)
                .isExtendedSize(false)
                .build();

        Program program = new Program();
        program.setProgramId(1);
        program.setProgramName("RLS");
        job.setProgram(program);

        StagedRow row = StagedRow.builder()
                .siteCode("EYR71")
                .siteName("South East Slade Point")
                .longitude("154")
                .latitude("-35")
                .date("16/11/20")
                .time("11:32")
                .diver("JEP")
                .build();

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
        row.setMeasureJson(new HashMap<>() {
            {
                put(1, "1");
                put(2, "12");
                put(13, "1");
            }
        });

        Collection<ObservableItem> species = observableItemRepository
                .getAllSpeciesNamesMatching(Collections.singletonList(row.getSpecies()));
        Collection<StagedRowFormatted> validatedRows = speciesFormatting.formatRowsWithSpecies(List.of(row),
                species);

        assertEquals(1, validatedRows.size());
        StagedRowFormatted formattedRow = (StagedRowFormatted) validatedRows.toArray()[0];

        Assertions.assertEquals("EYR71", formattedRow.getSite().getSiteCode());
        Assertions.assertEquals("South East Slade Point", formattedRow.getSite().getSiteName());
        Assertions.assertEquals(154, formattedRow.getLongitude());
        Assertions.assertEquals(-35, formattedRow.getLatitude());
        Assertions.assertEquals(LocalDate.parse("16/11/2020", DateTimeFormatter.ofPattern("d/M/yyyy")), formattedRow.getDate());
        Assertions.assertEquals(TimeUtils.parseTime("11:32"), formattedRow.getTime());
        Assertions.assertEquals("JEP", formattedRow.getDiver().getInitials());
        Assertions.assertEquals("Juan Espanol Pagina", formattedRow.getDiver().getFullName());
        Assertions.assertEquals(1, formattedRow.getBlock());
        Assertions.assertEquals(Directions.NE, formattedRow.getDirection());

        Assertions.assertTrue(formattedRow.getSpecies().isPresent());
        Assertions.assertEquals(102, formattedRow.getSpecies().get().getAphiaId());
        Assertions.assertEquals(1, formattedRow.getMeasureJson().get(13));
        Assertions.assertEquals(Optional.of(10.0), formattedRow.getVis());
        Assertions.assertEquals(4, formattedRow.getSurveyNum());
        Assertions.assertEquals(7, formattedRow.getDepth());
    }

    @Test
    void invalidMappingsShouldBeNull() {

        StagedJob job = new StagedJob();
        job.setId(1L);
        job.setIsExtendedSize(false);
        Program program = new Program();
        program.setProgramId(1);
        program.setProgramName("RLS");
        job.setProgram(program);

        StagedRow row = new StagedRow();
        row.setStagedJob(job);
        row.setBlock("1]");
        row.setCode("]]]]]");
        row.setDate("The Past");
        row.setDepth("Very Deep");
        row.setDirection("Directly under the earth's sun");
        row.setDiver("");
        row.setInverts("0]");
        row.setLatitude("BBB");
        row.setLongitude("AAA");
        row.setMethod("]]]]]]");
        row.setPqs("Nonexistent PQ diver");
        row.setSiteCode("");
        row.setSpecies("Nonexistent Species");
        row.setTime("Eleven:Thirty");
        row.setTotal("]]]]]]");
        row.setVis("Very Murky");
        row.setMeasureJson(new HashMap<>() {
            {
                put(13, "1]");
            }
        });

        Collection<ObservableItem> species = observableItemRepository
                .getAllSpeciesNamesMatching(Collections.singletonList(row.getSpecies()));

        Collection<StagedRowFormatted> validatedRows = speciesFormatting
                .formatRowsWithSpecies(List.of(row), species);

        assertEquals(1, validatedRows.size());

        StagedRowFormatted formattedRow = (StagedRowFormatted) validatedRows.toArray()[0];

        Assertions.assertNull(formattedRow.getBlock());
        Assertions.assertNull(formattedRow.getDate());
        Assertions.assertNull(formattedRow.getDepth());
        Assertions.assertNull(formattedRow.getDirection());
        Assertions.assertNull(formattedRow.getDiver());
        Assertions.assertNull(formattedRow.getInverts());
        Assertions.assertNull(formattedRow.getLatitude());
        Assertions.assertNull(formattedRow.getLongitude());
        Assertions.assertNull(formattedRow.getMethod());
        Assertions.assertNull(formattedRow.getPqs());
        Assertions.assertNull(formattedRow.getSite());
        Assertions.assertNull(formattedRow.getTotal());
        Assertions.assertNull(formattedRow.getMeasureJson().get(13));

        Assertions.assertFalse(formattedRow.getSpecies().isPresent());
        Assertions.assertFalse(formattedRow.getTime().isPresent());
        Assertions.assertFalse(formattedRow.getVis().isPresent());
    }

    @Test
    void onlyMeasurementsGreaterThanZeroShouldBeMapped() {
            
            StagedJob job = new StagedJob();
            job.setId(1L);
            job.setIsExtendedSize(false);
            Program program = new Program();
            program.setProgramId(1);
            program.setProgramName("RLS");
            job.setProgram(program);
            StagedRow row = new StagedRow();
            row.setStagedJob(job);
            row.setMeasureJson(new HashMap<>() {
                {
                    put(1, "0");
                    put(2, "222");
                    put(3, "0");
                    put(4, "444");
                    put(5, "0");
                }
            });
    
            Collection<ObservableItem> species = observableItemRepository
                    .getAllSpeciesNamesMatching(Collections.singletonList(row.getSpecies()));
            Collection<StagedRowFormatted> validatedRows = speciesFormatting.formatRowsWithSpecies(List.of(row),
                    species);

            StagedRowFormatted formattedRow = (StagedRowFormatted) validatedRows.toArray()[0];
            Assertions.assertEquals(2, formattedRow.getMeasureJson().size());
            Assertions.assertEquals(222, formattedRow.getMeasureJson().get(2));
            Assertions.assertEquals(444, formattedRow.getMeasureJson().get(4));
    }
}
