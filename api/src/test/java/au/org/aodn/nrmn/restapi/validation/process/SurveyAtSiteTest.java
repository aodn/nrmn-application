package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.service.validation.SiteValidation;

class SurveyAtSiteTest extends FormattedTestProvider {

    @InjectMocks
    SiteValidation siteValidation;
    
    @Test
    void sameCoordsShouldSucceed() {
        var formatted = getDefaultFormatted().build();

        formatted.setLatitude(-42.886410468013004);
        formatted.setLongitude(147.33520415427964);

        formatted.setSite(Site.builder().siteCode("A SITE").latitude( -42.886410468013004).longitude(147.33520415427964).build());
        var error = siteValidation.validateSurveyAtSite(formatted);
        assertNull(error);
    }

    @Test
    void differentCoordsShouldFail() {
        var formatted = getDefaultFormatted().build();
        formatted.setMethod(1);

        //hobart IMAS -42.886410468013004, 147.33520415427964
        formatted.setLatitude( -42.886410468013004);
        formatted.setLongitude(147.33520415427964);

        //Hobart Dpt. Treasury and Finance -42.88397415318471, 147.3293531695972
        formatted.setSite(Site.builder().siteCode("A SITE")
                .latitude( -42.88397415318471)
                .longitude(147.3293531695972).build());

        var error = siteValidation.validateSurveyAtSite(formatted);
        assertNotNull(error);
        assertTrue(error.getMessage().startsWith("Survey coordinates"));
        assertTrue(error.getColumnNames().contains("latitude"));
        assertTrue(error.getColumnNames().contains("longitude"));
    }

}
