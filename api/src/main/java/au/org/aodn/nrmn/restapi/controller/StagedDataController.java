package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.repository.StagedSurveyEntityRepository;
import au.org.aodn.nrmn.restapi.service.SpreadSheetService;
import au.org.aodn.nrmn.restapi.service.model.SheetWithHeader;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import cyclops.companion.Monoids;
import cyclops.control.Maybe;
import cyclops.control.Validated;
import cyclops.data.Seq;
import lombok.val;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/stage")
public class StagedDataController {

    @Autowired
    SpreadSheetService sheetService;
    @Autowired
    StagedSurveyEntityRepository stagedSurveyRepo;

    @PostMapping("/upload")
    public ResponseEntity<?> uploTadFiles(
            @RequestParam("withInvertSize") Boolean withInvertSize,
            @RequestParam("file") MultipartFile[] files) {
        val validationHelper = new ValidatorHelpers();

        val validatedSheets = Stream.of(files)
                .flatMap(file ->
                        Maybe.attempt(() -> {
                        Validated<ErrorInput,Seq<SheetWithHeader>> valid =    sheetService.validExcelFile(
                                    file.getName() + "-" + System.currentTimeMillis(),
                                    new XSSFWorkbook(file.getInputStream()),
                                    withInvertSize).bimap(err -> err, Seq::of);
                        return valid;
                        })
                                .stream()
                ).reduce((acc, validator) ->
                        acc.combine(Monoids.seqConcat(), validator))
                .orElseGet(() ->
                        Validated.invalid(new ErrorInput("No File provided", "upload")));

        List<ErrorInput> errors = validationHelper.toErrorList(validatedSheets);

        return validatedSheets.fold(
                err -> ResponseEntity.unprocessableEntity().body(errors),
                sheets -> {
                    //Todo process the sheets here
                    List<StagedSurveyEntity> stagedSurveys = sheets.stream()
                            .flatMap(sheet -> sheetService.sheets2Staged(sheet).stream())
                            .collect(Collectors.toList());
                    stagedSurveyRepo.saveAll(stagedSurveys);
                    return ResponseEntity.status(HttpStatus.OK).build();
                }
        );
    }
}
