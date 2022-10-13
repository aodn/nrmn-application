package au.org.aodn.nrmn.restapi.controller;

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
    private static Logger logger = LoggerFactory.getLogger(TemplateController.class);

    @Autowired
    private TemplateService templateService;

    @GetMapping(path = "/template.zip", produces = "application/zip")
    public void getTemplateZip(final HttpServletResponse response,
                               @RequestParam(defaultValue = "") List<Integer> locations) {

        logger.info(LogInfo.withContext(String.format("downloading template zip")));

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            templateService.writeZip(outputStream, locations);

            response.getOutputStream().write(outputStream.toByteArray());
            response.flushBuffer();
        }
        catch (IOException e) {
            logger.error("Fail to get template zip file", e);
        }
    }
}
