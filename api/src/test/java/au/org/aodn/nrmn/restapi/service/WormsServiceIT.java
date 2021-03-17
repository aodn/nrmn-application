package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;


public class WormsServiceIT {

    @Test @Ignore
    public void fuzzySearchReturnsResults() {
        WebClient wormsClient = WebClient.create("https://www.marinespecies.org/rest");
        AphiaRefService wormsService = new WormsService(wormsClient);
        List<AphiaRef> results = wormsService.fuzzyNameSearch("Paratrachichthys trailli");
        assertThat(results.isEmpty(), is(false));
    }

}
