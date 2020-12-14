package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvServiceTest {
    @InjectMocks
    CsvService csvService;

    @Mock
    DiverRepository diverRepository;


    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);

        when(diverRepository.findAll()).thenReturn(
                Arrays.asList(
                        Diver.builder().initials("GWB").fullName("George Bush").build(),
                        Diver.builder().initials("BHO").fullName("Barrack Obama").build(),
                        Diver.builder().initials("DJT").fullName("Donald Trump").build(),
                        Diver.builder().initials("101").fullName("Robopresident").build()
                ));

    }

    @Test
    void getDiversCsv() throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        csvService.getDiversCsv(printWriter);
        List<String> csvLines = Arrays.stream(stringWriter.toString().split("\n"))
                .map(String::trim).collect(Collectors.toList());

        assertEquals(4, csvLines.size());
        assertEquals("INITIALS,FULL NAME", csvLines.get(0));
        assert(csvLines.contains("GWB,George Bush"));
        assert(csvLines.contains("BHO,Barrack Obama"));
        assert(csvLines.contains("DJT,Donald Trump"));

    }
}
