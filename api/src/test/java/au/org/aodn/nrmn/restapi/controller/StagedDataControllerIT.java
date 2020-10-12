package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.controller.utils.RequestWrapper;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.repository.StagedSurveyEntityRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
@Testcontainers
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cicd")
class StagedDataControllerIT {
    @Autowired
    public TestRestTemplate testRestTemplate;
    @LocalServerPort
    int randomServerPort;

    @Autowired
    StagedSurveyEntityRepository surveyRepo;

    @Test
    public void UploadingCorrectIngestFileShouldbeOK() throws Exception{
        val reqUpload = new RequestWrapper<LinkedMultiValueMap<String, Object>,Void>();
        val file = new FileSystemResource("src/test/resources/sheets/correctShortHeader.xlsx");
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", file);
        parameters.add("withInvertSize", false);

       val resp = reqUpload
                .withContentType(MediaType.MULTIPART_FORM_DATA)
                .withEntity(parameters)
                .withMethod(HttpMethod.POST)
                .withUri(_createUrl("/api/stage/upload"))
                .build(testRestTemplate);
        assertEquals(resp.getStatusCode(), HttpStatus.OK);
        assertEquals(surveyRepo.findAll().size(), 1086);
    }

    private String _createUrl(String uri) {
        return "http://localhost:" + randomServerPort + uri;
    }
}