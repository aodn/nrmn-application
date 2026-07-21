package au.org.aodn.nrmn.restapi.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import au.org.aodn.nrmn.restapi.service.upload.S3IO;

@ExtendWith(MockitoExtension.class)
public class MaterializedViewServiceTest {

    private static final String RLS_KEY = "RlsKey00000000000001";
    private static final String TPAC_KEY = "TpacKey0000000000001";

    @Mock
    S3IO s3IO;

    @InjectMocks
    MaterializedViewService materializedViewService;

    private void setKeys(String rlsKey, String tpacKey) {
        ReflectionTestUtils.setField(materializedViewService, "rlsKey", rlsKey);
        ReflectionTestUtils.setField(materializedViewService, "tpacKey", tpacKey);
    }

    private void publishObfuscatedEndpoints() {
        ReflectionTestUtils.invokeMethod(materializedViewService, "publishObfuscatedEndpoints");
    }

    @Test
    public void publishesTheThreeRlsViewsUnderOneKeyAndTpacUnderItsOwn() {
        setKeys(RLS_KEY, TPAC_KEY);

        publishObfuscatedEndpoints();

        verify(s3IO).copyEndpoint("ep_species_survey_observation", RLS_KEY);
        verify(s3IO).copyEndpoint("ep_species_survey", RLS_KEY);
        verify(s3IO).copyEndpoint("ep_species_list", RLS_KEY);
        verify(s3IO).copyEndpoint("ep_tpac", TPAC_KEY);
        verifyNoMoreInteractions(s3IO);
    }

    @Test
    public void blankKeysSkipPublishingEntirely() {
        setKeys("", "");

        publishObfuscatedEndpoints();

        verifyNoInteractions(s3IO);
    }

    @Test
    public void aFailingViewDoesNotStopTheRest() {
        setKeys(RLS_KEY, TPAC_KEY);
        org.mockito.Mockito.doThrow(new RuntimeException("failed to write to S3"))
                .when(s3IO).copyEndpoint("ep_species_survey", RLS_KEY);

        publishObfuscatedEndpoints();

        verify(s3IO).copyEndpoint("ep_species_list", RLS_KEY);
        verify(s3IO).copyEndpoint("ep_tpac", TPAC_KEY);
    }

    @Test
    public void tpacStillPublishesWhenOnlyTheRlsKeyIsMissing() {
        setKeys("", TPAC_KEY);

        publishObfuscatedEndpoints();

        verify(s3IO).copyEndpoint("ep_tpac", TPAC_KEY);
        verifyNoMoreInteractions(s3IO);
    }
}
