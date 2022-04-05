package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

class SurveyAtSiteTest extends FormattedTestProvider {

    @Test
    void sameCoordsShouldSucceed() {
        StagedRowFormatted formatted = getDefaultFormatted().build();

        formatted.setLatitude(-42.886410468013004);
        formatted.setLongitude(147.33520415427964);

        formatted.setSite(Site.builder().siteCode("A SITE").latitude( -42.886410468013004).longitude(147.33520415427964).build());
        Collection<ValidationError> errors = validationProcess.checkData("ATRC", false, Arrays.asList(formatted));
        assertFalse(errors.stream().anyMatch(p -> p.getMessage().startsWith("Survey coordinates") && p.getColumnNames().contains("latitude")));
        assertFalse(errors.stream().anyMatch(p -> p.getMessage().startsWith("Survey coordinates") && p.getColumnNames().contains("longitude")));
    }

    @Test
    void differentCoordsShouldFail() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(1);

        //hobart IMAS -42.886410468013004, 147.33520415427964
        formatted.setLatitude( -42.886410468013004);
        formatted.setLongitude(147.33520415427964);

        //Hobart Dpt. Treasury and Finance -42.88397415318471, 147.3293531695972
        formatted.setSite(Site.builder().siteCode("A SITE")
                .latitude( -42.88397415318471)
                .longitude(147.3293531695972).build());

        Collection<ValidationError> errors = validationProcess.checkData("ATRC", false, Arrays.asList(formatted));
        assertTrue(errors.stream().anyMatch(p -> p.getMessage().startsWith("Survey coordinates") && p.getColumnNames().contains("latitude")));
        assertTrue(errors.stream().anyMatch(p -> p.getMessage().startsWith("Survey coordinates") && p.getColumnNames().contains("longitude")));
    }

}
