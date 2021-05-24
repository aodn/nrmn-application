package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.service.model.SpeciesRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class WormsServiceIT {

    @Test @Disabled
    public void partialSearchReturnsResults() {
        WebClient wormsClient = WebClient.create("https://www.marinespecies.org/rest");
        WormsService wormsService = new WormsService(wormsClient);
        List<SpeciesRecord> results = wormsService.partialSearch(0, "Paratrachichthys trailli");
        assertThat(results.isEmpty(), is(false));
    }

}
