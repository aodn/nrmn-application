package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.Program;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.process.ValidationProcess;
import software.amazon.awssdk.utils.ImmutableMap;

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
                                .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(3, 7).build())
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
                                .measureJson(ImmutableMap.<Integer, Integer>builder().build())
                                .ref(ref);
        }
}
