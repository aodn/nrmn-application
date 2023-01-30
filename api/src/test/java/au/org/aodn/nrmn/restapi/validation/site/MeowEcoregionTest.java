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
    public void speciesInMeowTest() {
        StagedRowFormatted rowFormatted = new StagedRowFormatted();
        rowFormatted.setSite(Site.builder().siteId(1).build());
        rowFormatted.setMethod(2);
        rowFormatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("INSIDE").build()));
        var error = siteValidation.validateSites(Arrays.asList(rowFormatted));
        Assert.isTrue(error.size() == 0);
    }

    @Test
    public void speciesOutsideMeowTest() {
        StagedRowFormatted rowFormatted = new StagedRowFormatted();
        rowFormatted.setSite(Site.builder().siteId(1).build());
        rowFormatted.setMethod(2);
        rowFormatted.setSpecies(Optional.of(ObservableItem.builder().observableItemName("OUTSIDE").build()));
        var error = siteValidation.validateSites(Arrays.asList(rowFormatted));
        Assert.notEmpty(error);
    }
}
