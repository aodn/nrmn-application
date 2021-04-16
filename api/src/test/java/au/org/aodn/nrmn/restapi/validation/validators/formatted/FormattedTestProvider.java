package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import software.amazon.awssdk.utils.ImmutableMap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public  class FormattedTestProvider {

    protected StagedRowFormatted.StagedRowFormattedBuilder  getDefaultFormatted() {
        StagedRow ref = StagedRow.builder()
                .stagedJob(StagedJob.builder()
                        .isExtendedSize(true)
                        .program(Program.builder().programName("PROJECT")
                                .build()).build()).build();

        Diver diver = Diver.builder().initials("SAM").build();
        return StagedRowFormatted.builder()
                .block(1)
                .method(2)
                .diver(diver)
                .buddy(Diver.builder().initials("MAX").build())
                .species(ObservableItem.builder().observableItemName("THE SPECIES").build())
                .site(Site.builder().siteCode("A SITE").build())
                .depth(1)
                .surveyNum(Optional.of(2))
                .direction(Directions.N)
                .vis(Optional.of(15))
                .date(LocalDate.of(2003, 03, 03))
                .time(Optional.of(LocalTime.of(12, 34, 56)))
                .pqs(diver)
                .m2InvertSizingSpecies(5)
                .isInvertSizing(true)
                .l5(2.1)
                .l95(7.2)
                .lMax(50)
                .code("AAA")
                .measureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(3, 7).build())
                .ref(ref);
    }
}
