package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.Site;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Within200MSiteCheckTest extends FormattedTestProvider {

    @Test
    void within200MShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        //hobart IMAS -42.886410468013004, 147.33520415427964
        formatted.setLatitude( -42.886410468013004);
        formatted.setLongitude(147.33520415427964);

        //Hobart Blue Eye SeaFood228 -42.88654698514, 147.33479357370092
        formatted.setSite(Site.builder().siteCode("A SITE").latitude( -42.88654698514).longitude(147.33479357370092).build());

         val validator =   new Within200MSiteCheck();
        val res = validator.valid(formatted);
        assertTrue(res.isValid());
    }

    @Test
    void beyound200MShouldFail() {
        val formatted = getDefaultFormatted().build();
        //hobart IMAS -42.886410468013004, 147.33520415427964
        formatted.setLatitude( -42.886410468013004);
        formatted.setLongitude(147.33520415427964);

        //Hobart Dpt. Treasury and Finance -42.88397415318471, 147.3293531695972
        formatted.setSite(Site.builder().siteCode("A SITE")
                .latitude( -42.88397415318471)
                .longitude(147.3293531695972).build());

        val validator =   new Within200MSiteCheck();
        val res = validator.valid(formatted);
        assertTrue(res.isInvalid());
    }

}
