package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

class Within200MSiteCheckTest extends FormattedTestProvider {

    @Test
    void within200MShouldSuccess() {
        StagedRowFormatted formatted = getDefaultFormatted().build();

        // Hobart IMAS: -42.886410468013004, 147.33520415427964
        formatted.setLatitude( -42.886410468013004);
        formatted.setLongitude(147.33520415427964);

        //Hobart Blue Eye SeaFood: 42.88654698514, 147.33479357370092
        formatted.setSite(Site.builder().siteCode("A SITE").latitude( -42.88654698514).longitude(147.33479357370092).build());
        Collection<ValidationError> errors = validationProcess.checkData("ATRC", false, Arrays.asList(formatted));
        assertFalse(errors.stream().anyMatch(p -> p.getMessage().contains("Coordinates are further than 0.2km from the Site")));
    }

    @Test
    void outside200MShouldFail() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(1);
        // formatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("THE SPECIES").methods(methods).build()));

        //hobart IMAS -42.886410468013004, 147.33520415427964
        formatted.setLatitude( -42.886410468013004);
        formatted.setLongitude(147.33520415427964);

        //Hobart Dpt. Treasury and Finance -42.88397415318471, 147.3293531695972
        formatted.setSite(Site.builder().siteCode("A SITE")
                .latitude( -42.88397415318471)
                .longitude(147.3293531695972).build());

        Collection<ValidationError> errors = validationProcess.checkData("ATRC", false, Arrays.asList(formatted));
        assertTrue(errors.stream().anyMatch(p -> p.getMessage().contains("Coordinates are further than 0.2km from the Site")));
    }

}
