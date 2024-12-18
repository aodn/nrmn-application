package au.org.aodn.nrmn.restapi.integration;

import au.org.aodn.nrmn.restapi.controller.IngestionController;
import au.org.aodn.nrmn.restapi.data.model.Observation;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Testcontainers
@Transactional
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
public class SurveyIngestionIT {

    protected Logger log = LoggerFactory.getLogger(SurveyIngestionIT.class);

    @Autowired
    IngestionController ingestionController;

    @Autowired
    StagedRowRepository rowRepository;

    @Autowired
    ObservationRepository observationRepository;

    @Test
    @WithUserDetails("test@example.com")
    @Sql({"/sql/drop_nrmn.sql",
            "/sql/migration.sql",
            "/sql/application.sql",
            "/testdata/FILL_ROLES.sql",
            "/testdata/TEST_USER.sql",
            "/testdata/FILL_DATA.sql",
            "/testdata/job6.sql"})
    public void verifyIngestionCorrect() {

        // Make sure the items are set
        List<StagedRow> row = rowRepository.findRowsByJobId(6L);
        /*
         * This test case contains an entry "Survey not Done", so you should see fewer entries,
         */
        Assertions.assertEquals(5, row.size(), "Needed record in the db");

        Set<Integer> existingObservations = observationRepository.findAll()
                .stream().map(Observation::getObservationId).collect(Collectors.toSet());

        ResponseEntity<Map<String, Object>> v = ingestionController.ingest(6L);
        Assertions.assertEquals(HttpStatus.OK, v.getStatusCode(), "Ingesting OK");

        // We need to loop and check if job completed
        Assertions.assertEquals(StagedJobEventType.INGESTING, Objects.requireNonNull(v.getBody()).get("jobStatus"), "Status correct");
        Long logId = Long.parseLong(Objects.requireNonNull(v.getBody()).get("jobLogId").toString());

        boolean done = Boolean.FALSE;
        CountDownLatch latch = new CountDownLatch(1);
        ResponseEntity<Map<String, Object>> job;

        while(!done) {
            try {
                latch.await(1, TimeUnit.SECONDS);
                job = ingestionController.getIngest(logId);

                Assertions.assertEquals(HttpStatus.OK, job.getStatusCode(), "Ingesting OK");
                StagedJobEventType eventType = (StagedJobEventType)Objects.requireNonNull(job.getBody()).get("jobStatus");

                if(eventType == StagedJobEventType.ERROR) {
                    throw new Exception("Job type is error");
                }
                else {
                    done = (eventType == StagedJobEventType.INGESTED);
                }
            }
            catch(Exception e) {
                Assertions.fail("Should not get exception here", e);
            }
        }

        // Check the result of ingest, remove those existing one
        List<Observation> observations = observationRepository.findAll()
                .stream()
                .filter(f -> !existingObservations.contains(f.getObservationId()))
                .collect(Collectors.toList());

        Assertions.assertEquals(8, observations.size(), "Inserted match");

        List<Observation> twelvePointFive = observations.stream().filter(f -> f.getMeasure().getMeasureName().equalsIgnoreCase("12.5cm")).collect(Collectors.toList());
        Assertions.assertEquals(2, twelvePointFive.size(), "12.5cm size correct");
        Assertions.assertEquals(5, twelvePointFive.get(0).getMeasure().getSeqNo(), "12.5cm seqno correct 0");
        Assertions.assertEquals(4, twelvePointFive.get(0).getMeasureValue(), "12.5cm measure correct 0");
        Assertions.assertEquals(5, twelvePointFive.get(1).getMeasure().getSeqNo(), "12.5cm seqno correct 1");
        Assertions.assertEquals(1, twelvePointFive.get(1).getMeasureValue(), "12.5cm measure correct 1");

        List<Observation> fifteen = observations.stream().filter(f -> f.getMeasure().getMeasureName().equalsIgnoreCase("15cm")).collect(Collectors.toList());
        Assertions.assertEquals(1, fifteen.size(), "15cm size correct");
        Assertions.assertEquals(6, fifteen.get(0).getMeasure().getSeqNo(), "15cm seqno correct");
        Assertions.assertEquals(1, fifteen.get(0).getMeasureValue(), "15cm measure correct");

        List<Observation> twenty = observations.stream().filter(f -> f.getMeasure().getMeasureName().equalsIgnoreCase("20cm")).collect(Collectors.toList());
        Assertions.assertEquals(1, twenty.size(), "20cm size correct");
        Assertions.assertEquals(7, twenty.get(0).getMeasure().getSeqNo(), "20cm seqno correct");
        Assertions.assertEquals(2, twenty.get(0).getMeasureValue(), "20cm measure correct");

        List<Observation> twentyFive = observations.stream().filter(f -> f.getMeasure().getMeasureName().equalsIgnoreCase("25cm")).collect(Collectors.toList());
        Assertions.assertEquals(1, twentyFive.size(), "25cm size correct");
        Assertions.assertEquals(8, twentyFive.get(0).getMeasure().getSeqNo(), "25cm seqno correct");
        Assertions.assertEquals(2, twentyFive.get(0).getMeasureValue(), "25cm measure correct");

        List<Observation> thirty = observations.stream().filter(f -> f.getMeasure().getMeasureName().equalsIgnoreCase("30cm")).collect(Collectors.toList());
        Assertions.assertEquals(1, thirty.size(), "30cm size correct");
        Assertions.assertEquals(9, thirty.get(0).getMeasure().getSeqNo(), "30cm seqno correct");
        Assertions.assertEquals(1, thirty.get(0).getMeasureValue(), "30cm measure correct");

        List<Observation> thirtyFive = observations.stream().filter(f -> f.getMeasure().getMeasureName().equalsIgnoreCase("35cm")).collect(Collectors.toList());
        Assertions.assertEquals(1, thirtyFive.size(), "35cm size correct");
        Assertions.assertEquals(10, thirtyFive.get(0).getMeasure().getSeqNo(), "35cm seqno correct");
        Assertions.assertEquals(1, thirtyFive.get(0).getMeasureValue(), "35cm measure correct");
        Assertions.assertEquals("Antonia Cooper", thirtyFive.get(0).getDiver().getFullName(), "50cm diver correct");

        List<Observation> fifty = observations.stream().filter(f -> f.getMeasure().getMeasureName().equalsIgnoreCase("50cm")).collect(Collectors.toList());
        Assertions.assertEquals(1, fifty.size(), "50cm size correct");
        Assertions.assertEquals(12, fifty.get(0).getMeasure().getSeqNo(), "50cm seqno correct");
        Assertions.assertEquals(1, fifty.get(0).getMeasureValue(), "50cm measure correct");
        Assertions.assertEquals("Antonia Cooper", fifty.get(0).getDiver().getFullName(), "50cm diver correct");
    }
}
