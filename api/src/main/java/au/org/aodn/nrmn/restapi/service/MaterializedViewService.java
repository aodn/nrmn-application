package au.org.aodn.nrmn.restapi.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import au.org.aodn.nrmn.restapi.data.repository.MaterializedViewsRepository;
import au.org.aodn.nrmn.restapi.service.upload.S3IO;

@Component
@EnableAsync
public class MaterializedViewService {

    private static final Logger logger = LoggerFactory.getLogger(MaterializedViewService.class);

    @Autowired
    private Environment environment;

    @Autowired
    private S3IO s3IO;

    @Autowired
    MaterializedViewsRepository materializedViewsRepository;

    private void uploadMaterializedView(String viewName, List<Tuple> viewResult) throws IOException {
        var headers = viewResult.get(0).getElements().stream().map(e -> e.getAlias()).collect(Collectors.toList());
        var values = viewResult.stream().map(e -> e.toArray()).collect(Collectors.toList());
        var headerFormat = CSVFormat.Builder.create().setHeader(headers.toArray(new String[0])).build();
        var tempFile = File.createTempFile(viewName, ".csv");
        try (var fileWriter = new FileWriter(tempFile)) {
            try (var csvPrinter = new CSVPrinter(fileWriter, headerFormat)) {
                csvPrinter.printRecords(values);
            }
            s3IO.uploadMaterializedView(viewName, tempFile);
        }
        Files.deleteIfExists(tempFile.toPath());
    }

    private void uploadEpM1() {
        try {
            var offset = 0;
            var limit = 100000;
            var viewName = "ep_m1";
            var max = materializedViewsRepository.countEpM1();
            var viewResult = materializedViewsRepository.getEpM1(offset, limit);
            var headers = viewResult.get(0).getElements().stream().map(e -> e.getAlias()).collect(Collectors.toList());
            var initialValues = viewResult.stream().map(e -> e.toArray()).collect(Collectors.toList());
            var headerFormat = CSVFormat.Builder.create().setHeader(headers.toArray(new String[0])).build();
            var tempFile = File.createTempFile(viewName, ".csv");
            try (var fileWriter = new FileWriter(tempFile)) {
                try (var csvPrinter = new CSVPrinter(fileWriter, headerFormat)) {
                    csvPrinter.printRecords(initialValues);
                    while (offset < max) {
                        viewResult = materializedViewsRepository.getEpM1(offset, limit);
                        var nextValues = viewResult.stream().map(e -> e.toArray()).collect(Collectors.toList());
                        csvPrinter.printRecords(nextValues);
                        offset += limit;
                    }
                }
            }
            s3IO.uploadMaterializedView(viewName, tempFile);
            Files.deleteIfExists(tempFile.toPath());
        } catch (Exception e) {
            logger.error("Failed to upload ep_m1", e);
        }
    }

    public void uploadAllMaterializedViews() {

        if (materializedViewsRepository.checkAnyRunning()) {
            logger.error("Failed to upload materialized views as one or more are still refreshing.");
            return;
        }

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            uploadMaterializedView("ui_species_attributes",
                    materializedViewsRepository.getUiSpeciesAttributes());
            uploadMaterializedView("ep_m2_cryptic_fish",
                    materializedViewsRepository.getEpM2CrypticFish());
            uploadMaterializedView("ep_m2_inverts",
                    materializedViewsRepository.getEpM2Inverts());
            uploadMaterializedView("ep_observable_items",
                    materializedViewsRepository.getEpObservableItems());
            uploadMaterializedView("ep_rarity_abundance",
                    materializedViewsRepository.getEpRarityAbundance());
            uploadMaterializedView("ep_rarity_extents",
                    materializedViewsRepository.getEpRarityExtents());
            uploadMaterializedView("ep_rarity_range",
                    materializedViewsRepository.getEpRarityRange());
            uploadMaterializedView("ep_site_list",
                    materializedViewsRepository.getEpSiteList());
            uploadMaterializedView("ep_survey_list",
                    materializedViewsRepository.getEpSurveyList());
            uploadMaterializedView("ep_rarity_frequency",
                    materializedViewsRepository.getEpRarityFrequency());

            uploadEpM1();

            stopWatch.stop();
            logger.info("Uploaded all materialized views in " + stopWatch.getLastTaskTimeMillis() + "ms");
        } catch (Exception e) {
            logger.error("Failed to upload all materialized views", e);
        }
    }

    @Async
    public void refreshAllMaterializedViews() {

        if (environment.getActiveProfiles().length > 0) {
            logger.info("Skipping refreshAllMaterializedViews as active profile set.");
            return;
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (!materializedViewsRepository.checkUiSpeciesAttributesRunning())
            materializedViewsRepository.refreshUiSpeciesAttributes();

        if (!materializedViewsRepository.checkEpM2CrypticFishRunning())
            materializedViewsRepository.refreshEpM2CrypticFish();

        if (!materializedViewsRepository.checkEpM2InvertsRunning())
            materializedViewsRepository.refreshEpM2Inverts();

        if (!materializedViewsRepository.checkEpObservableItemsRunning())
            materializedViewsRepository.refreshEpObservableItems();

        if (!materializedViewsRepository.checkEpRarityAbundanceRunning())
            materializedViewsRepository.refreshEpRarityAbundance();

        if (!materializedViewsRepository.checkEpRarityExtentsRunning())
            materializedViewsRepository.refreshEpRarityExtents();

        if (!materializedViewsRepository.checkEpRarityRangeRunning())
            materializedViewsRepository.refreshEpRarityRange();

        if (!materializedViewsRepository.checkEpSiteListRunning())
            materializedViewsRepository.refreshEpSiteList();

        if (!materializedViewsRepository.checkEpSurveyListRunning())
            materializedViewsRepository.refreshEpSurveyList();

        if (!materializedViewsRepository.checkEpM1Running())
            materializedViewsRepository.refreshEpM1();

        if (!materializedViewsRepository.checkEpRarityFrequencyRunning())
            materializedViewsRepository.refreshEpRarityFrequency();

        stopWatch.stop();
        logger.info("Endpoints refreshed in {}s", stopWatch.getTotalTimeSeconds());

    }
}
