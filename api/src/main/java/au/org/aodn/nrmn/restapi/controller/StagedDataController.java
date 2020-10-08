package au.org.aodn.nrmn.restapi.controller;

import lombok.val;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/stage")
public class StagedDataController {

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        List<String> fileNames = new ArrayList<>();
        Arrays.asList(files).stream().map(file -> {
            try {
                //TOdo logic to valid and save file
                InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream);
            } catch (Exception e) {
            }
        });
        return ResponseEntity.status(HttpStatus.OK).build();

    }
}
