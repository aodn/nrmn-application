package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.data.HashMap;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RawValidationTest {

    @Test
    void returnsFormattedRowForSnd() {
        java.util.HashMap<String, Object> values = new java.util.HashMap<>();
        values.put("Vis", Optional.of(6));
        values.put("Method", 2);
        values.put("Depth", 5.2);
        values.put("Total", 1);
        values.put("MeasureJson", Collections.emptyMap());
        values.put("Site", Site.builder().siteCode("SIT01").build());
        values.put("SpeciesNotfound", false);
        values.put("Latitude", -42.72013855);
        values.put("Block", 2);
        values.put("Code", "SND");
        values.put("Time", Optional.of(LocalTime.NOON));
        values.put("Date", LocalDate.parse("2019-04-02"));
        values.put("Longitude", 148.0114594);
        values.put("Ref", StagedRow.builder().id(1L).stagedJob(StagedJob.builder().id(1L).build()).build());
        values.put("Species", null);
        values.put("Inverts", Optional.of(0));
        values.put("Diver", Diver.builder().initials("CCJ").build());
        values.put("P-Qs", Diver.builder().initials("CCJ").build());
        values.put("IsInvertSizing", Optional.of(false));
        values.put("Direction", Directions.N);

        RawValidation rawValidation = new RawValidation();

        StagedRowFormatted formattedRow = rawValidation.toFormat(HashMap.fromMap(values), false);

        assertEquals("SND", formattedRow.getCode());
    }
}
