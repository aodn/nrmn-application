package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.service.model.SpeciesRecord;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class WormsServiceIT {

    @Test @Ignore
    public void partialSearchReturnsResults() {
        WebClient wormsClient = WebClient.create("https://www.marinespecies.org/rest");
        WormsService wormsService = new WormsService(wormsClient);
        List<SpeciesRecord> results = wormsService.partialSearch("Paratrachichthys trailli");
        assertThat(results.isEmpty(), is(false));
    }

}
