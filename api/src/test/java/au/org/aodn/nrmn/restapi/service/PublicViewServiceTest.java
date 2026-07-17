package au.org.aodn.nrmn.restapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.util.ReflectionTestUtils;

import au.org.aodn.nrmn.restapi.service.upload.S3IO;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PublicViewServiceTest {

    @Mock
    DataSource dataSource;

    @Mock
    Connection connection;

    @Mock
    Statement statement;

    @Mock
    ResultSet resultSet;

    @Mock
    ResultSetMetaData resultSetMetaData;

    @Mock
    S3IO s3IO;

    @InjectMocks
    PublicViewService publicViewService;

    @Captor
    ArgumentCaptor<String> objectNameCaptor;

    @BeforeEach
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(publicViewService, "sourceSchema", "nrmn");

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(1);
        when(resultSetMetaData.getColumnLabel(1)).thenReturn("survey_id");
        // no rows, the CSV still gets a header and is still uploaded
        when(resultSet.next()).thenReturn(false);
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
    public void oneFailingViewDoesNotStopTheRest() {
        doThrow(new RuntimeException("failed to write to S3"))
                .when(s3IO).uploadPublicView(objectNameCaptor.capture(), any(File.class));

        publicViewService.publishPublicViews();

        verify(s3IO, times(7)).uploadPublicView(anyString(), any(File.class));
    }
}
