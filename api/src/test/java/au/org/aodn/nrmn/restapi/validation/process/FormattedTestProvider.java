package au.org.aodn.nrmn.restapi.validation.process;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.data.model.Diver;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.Program;
import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.enums.Directions;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;

@ExtendWith(MockitoExtension.class)
public class FormattedTestProvider {

        @InjectMocks
        ValidationProcess validationProcess;

        protected StagedRowFormatted.StagedRowFormattedBuilder getDefaultFormatted() {
                StagedRow ref = StagedRow.builder()
                                .stagedJob(StagedJob.builder().isExtendedSize(true)
                                                .program(Program.builder().programName("PROJECT").build()).build())
                                .build();

                Diver diver = Diver.builder().initials("SAM").build();
                return StagedRowFormatted.builder().block(1).method(2).diver(diver)
                                .species(Optional
                                                .of(ObservableItem.builder().observableItemName("THE SPECIES").build()))
                                .site(Site.builder().siteCode("A SITE").build()).depth(1).surveyNum(2)
                                .direction(Directions.N).vis(Optional.of(15.5)).date(LocalDate.of(2003, 03, 03))
                                .time(Optional.of(LocalTime.of(12, 34, 56))).pqs(diver).isInvertSizing(true).code("AAA")
                                .measureJson(new HashMap<Integer, Integer>() {{
                                        put(1, 4);
                                        put(3, 7);
                                    }})
                                .ref(ref);
        }

        protected StagedRowFormatted.StagedRowFormattedBuilder getDezFormatted() {
                StagedRow ref = StagedRow.builder()
                                .stagedJob(StagedJob.builder().isExtendedSize(true)
                                                .program(Program.builder().programName("PROJECT").build()).build())
                                .build();

                Diver diver = Diver.builder().initials("SAM").build();
                return StagedRowFormatted.builder().block(1).method(2).diver(diver)
                                .species(Optional
                                                .of(ObservableItem.builder().observableItemName("THE SPECIES").build()))
                                .site(Site.builder().siteCode("A SITE").build()).depth(1).surveyNum(2)
                                .direction(Directions.N).vis(Optional.of(15.5)).date(LocalDate.of(2003, 03, 03))
                                .time(Optional.of(LocalTime.of(12, 34, 56))).pqs(diver).isInvertSizing(true).code("DEZ")
                                .measureJson(new HashMap<Integer, Integer>())
                                .ref(ref);
        }
}
