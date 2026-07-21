package au.org.aodn.nrmn.restapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import au.org.aodn.nrmn.restapi.data.repository.PublicViewsRepository;
import au.org.aodn.nrmn.restapi.service.upload.S3IO;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PublicViewServiceTest {

    @Mock
    PublicViewsRepository publicViewsRepository;

    @Mock
    S3IO s3IO;

    @InjectMocks
    PublicViewService publicViewService;

    @Captor
    ArgumentCaptor<String> objectNameCaptor;

    private Tuple aRow() {
        var element = mock(TupleElement.class);
        when(element.getAlias()).thenReturn("survey_id");

        var tuple = mock(Tuple.class);
        when(tuple.getElements()).thenReturn(List.of(element));
        when(tuple.toArray()).thenReturn(new Object[] { 1 });
        return tuple;
    }

    @BeforeEach
    public void setUp() {
        var page = List.of(aRow());
        when(publicViewsRepository.countEpM0OffTransectSightingPublic()).thenReturn(1L);
        when(publicViewsRepository.getEpM0OffTransectSightingPublic(anyInt(), anyInt())).thenReturn(page);
        when(publicViewsRepository.countEpM1Public()).thenReturn(1L);
        when(publicViewsRepository.getEpM1Public(anyInt(), anyInt())).thenReturn(page);
        when(publicViewsRepository.countEpM2CrypticFishPublic()).thenReturn(1L);
        when(publicViewsRepository.getEpM2CrypticFishPublic(anyInt(), anyInt())).thenReturn(page);
        when(publicViewsRepository.countEpM2InvertsPublic()).thenReturn(1L);
        when(publicViewsRepository.getEpM2InvertsPublic(anyInt(), anyInt())).thenReturn(page);
        when(publicViewsRepository.countEpM3IsqPublic()).thenReturn(1L);
        when(publicViewsRepository.getEpM3IsqPublic(anyInt(), anyInt())).thenReturn(page);
        when(publicViewsRepository.countEpSiteListPublic()).thenReturn(1L);
        when(publicViewsRepository.getEpSiteListPublic(anyInt(), anyInt())).thenReturn(page);
        when(publicViewsRepository.countEpSurveyListPublic()).thenReturn(1L);
        when(publicViewsRepository.getEpSurveyListPublic(anyInt(), anyInt())).thenReturn(page);
    }

    @Test
    public void publishesEveryPublicViewUnderDataSuffixedName() {
        publicViewService.publishPublicViews();

        verify(s3IO, times(7)).uploadPublicView(objectNameCaptor.capture(), any(File.class));

        assertEquals(List.of(
                "ep_m0_off_transect_sighting_public_data",
                "ep_m1_public_data",
                "ep_m2_cryptic_fish_public_data",
                "ep_m2_inverts_public_data",
                "ep_m3_isq_public_data",
                "ep_site_list_public_data",
                "ep_survey_list_public_data"), objectNameCaptor.getAllValues());
    }

    @Test
    public void anEmptyViewIsSkippedRatherThanUploadedEmpty() {
        when(publicViewsRepository.countEpM1Public()).thenReturn(0L);
        when(publicViewsRepository.getEpM1Public(anyInt(), anyInt())).thenReturn(List.of());

        publicViewService.publishPublicViews();

        verify(s3IO, times(6)).uploadPublicView(objectNameCaptor.capture(), any(File.class));
        assertFalse(objectNameCaptor.getAllValues().contains("ep_m1_public_data"));
    }

    @Test
    public void oneFailingUploadDoesNotStopTheRest() {
        doThrow(new RuntimeException("failed to write to S3"))
                .when(s3IO).uploadPublicView(anyString(), any(File.class));

        publicViewService.publishPublicViews();

        verify(s3IO, times(7)).uploadPublicView(anyString(), any(File.class));
    }
}
