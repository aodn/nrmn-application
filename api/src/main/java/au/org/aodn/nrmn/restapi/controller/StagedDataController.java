package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.service.SpreadSheetService;
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

    @PostMapping("/upload")
    public ResponseEntity<?> uploTadFiles(@RequestParam("file") MultipartFile[] files) {
        val validationHelper = new ValidatorHelpers();

        val validatedSheets = Stream.of(files)
                .flatMap(file ->
                        Maybe.attempt(() ->
                                sheetService.validExcelFile(
                                        new XSSFWorkbook(file.getInputStream()),
                                        false).bimap(err -> err, Seq::of))
                                .stream()
                ).reduce((acc, validator) ->
                        acc.combine(Monoids.seqConcat(), validator))
                .orElseGet(() ->
                        Validated.invalid(new ErrorInput("No File provided", "upload")));

        val errors = validationHelper.toErrorList(validatedSheets);

        return validatedSheets.fold(
                err -> ResponseEntity.unprocessableEntity().body(errors),
                sheets -> {
                    //Todo process the sheets here
                    return ResponseEntity.status(HttpStatus.OK).build();
                }
        );
    }
}
