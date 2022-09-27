package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.db.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.service.TemplateService;
import au.org.aodn.nrmn.restapi.util.LogInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/template")
@Tag(name = "Template Export")
public class TemplateController {
    private static Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    UserActionAuditRepository userActionAuditRepository;
    @Autowired
    private TemplateService templateService;

    @GetMapping(path = "/template.zip", produces = "application/zip")
    public void getTemplateZip(final HttpServletResponse response,
            @RequestParam(defaultValue = "") List<Integer> locations) throws IOException {
        logger.info(LogInfo.withContext(String.format("downloading template zip")));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        templateService.writeZip(outputStream, locations);

        try {
            response.getOutputStream().write(outputStream.toByteArray());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
        }
    }
}
