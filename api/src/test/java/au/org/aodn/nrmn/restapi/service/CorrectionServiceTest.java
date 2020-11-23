package au.org.aodn.nrmn.restapi.service;


import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CorrectionServiceTest {
    @Mock
    SurveyRepository surveyRepository;
    @Mock
    SurveyMethodRepository surveyMethodRepository;
    @Mock
    ObservationRepository observationRepository;
    @InjectMocks
    CorrectionService correctionService;

    Survey.SurveyBuilder surveyBuilder;
    SurveyMethod.SurveyMethodBuilder surveyMethodBuilder;
    Observation.ObservationBuilder observationBuilder;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);

        surveyBuilder = Survey.builder()
                .surveyDate(Date.valueOf("2003-03-03"))
                .surveyTime(Time.valueOf("12:34:56"))
                .surveyNum(3)
                .depth(2)
                .direction("NW")
                .program(Program.builder().programId(0).build())
                .site(Site.builder().siteName("A SITE").build())
                .visibility(1);
        surveyMethodBuilder = SurveyMethod.builder()
                .surveyNotDone(false)
                .blockNum(2)
                .method(Method.builder().methodName("Standard Fish").methodId(1).build())
                .survey(surveyBuilder.build())
                .surveyMethodId(0);

        when(surveyMethodRepository.findAll(any(Example.class))).thenReturn(Arrays.asList(surveyMethodBuilder.build()));
        observationBuilder = Observation.builder()
                .diver(Diver.builder().initials("SAM").build())
                .observableItem(ObservableItem.builder().aphiaRef(AphiaRef.builder().build()).build())
                .surveyMethod(surveyMethodBuilder.build());

        List<Object> observations = Arrays.asList(
                observationBuilder
                        .measure(Measure.builder().seqNo(0).build())
                        .measureValue(333).build(),
                observationBuilder
                        .measure(Measure.builder().seqNo(1).build())
                        .measureValue(666).build(),
                observationBuilder
                        .measure(Measure.builder().seqNo(2).build())
                        .measureValue(999).build());


        when(observationRepository.findAll(any(Example.class))).thenReturn(observations);
    }

    @Test
    void getRows() {
        List<StagedRow> rows = correctionService.convertSurveyToStagedRows(surveyBuilder.build())
                .collect(Collectors.toList());

        assertEquals(1, rows.size());

        StagedRow row = rows.get(0);
        assertEquals(2, row.getMeasureJson().size());
        assertEquals("333", row.getInverts());
        assertEquals("666", row.getMeasureJson().get(1));
        assertEquals("999", row.getMeasureJson().get(2));
    }

}
