package au.org.aodn.nrmn.restapi.validation.site;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.repository.MeowRegionsRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.service.validation.SiteValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import io.jsonwebtoken.lang.Assert;

@RunWith(MockitoJUnitRunner.class)
public class MeowEcoregionTest {

    @Mock
    MeowRegionsRepository meowRegionsRepository;

    @Mock
    SiteRepository siteRepository;

    @InjectMocks
    SiteValidation siteValidation;

    @Before
    public void setUp() {
        when(siteRepository.getEcoregion(1)).thenReturn("INSIDE");
        when(meowRegionsRepository.getEcoregionContains("INSIDE", Arrays.asList("INSIDE"))).thenReturn(Arrays.asList("INSIDE"));
        when(meowRegionsRepository.getEcoregionContains("INSIDE", Arrays.asList("OUTSIDE"))).thenReturn(Collections.emptyList());
    }

    @Test
    public void sndSkipMeowTest() {
        var snd = StagedRowFormatted.builder().site(Site.builder().siteId(1).build()).code("SND").build();
        var error = siteValidation.validateSites(Arrays.asList(snd));
        Assert.isTrue(error.isEmpty());
    }

    @Test
    public void dezSkipMeowTest() {
        var dez = StagedRowFormatted.builder().site(Site.builder().siteId(1).build()).code("DEZ").build();
        var error = siteValidation.validateSites(Arrays.asList(dez));
        Assert.isTrue(error.isEmpty());
    }

    @Test
    public void methodSkipMeowTest() {
        var site = Site.builder().siteId(1).build();
        var species = ObservableItem.builder().observableItemName("OUTSIDE").build();
        var row = StagedRowFormatted.builder().species(Optional.of(species)).site(site).method(99).build();
        var error = siteValidation.validateSites(Arrays.asList(row));
        Assert.isTrue(error.isEmpty());
    }

    @Test
    public void speciesInMeowTest() {
        var site = Site.builder().siteId(1).build();
        var species = ObservableItem.builder().observableItemName("INSIDE").build();
        var row = StagedRowFormatted.builder().species(Optional.of(species)).site(site).method(2).build();
        var error = siteValidation.validateSites(Arrays.asList(row));
        Assert.isTrue(error.isEmpty());
    }

    @Test
    public void speciesOutsideMeowTest() {
        var site = Site.builder().siteId(1).build();
        var species = ObservableItem.builder().observableItemName("OUTSIDE").build();
        var row = StagedRowFormatted.builder().species(Optional.of(species)).site(site).method(2).build();
        var error = siteValidation.validateSites(Arrays.asList(row));
        Assert.notEmpty(error);
    }
}
