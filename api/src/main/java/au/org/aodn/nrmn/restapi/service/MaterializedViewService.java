package au.org.aodn.nrmn.restapi.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Tuple;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import au.org.aodn.nrmn.restapi.data.repository.MaterializedViewsRepository;
import au.org.aodn.nrmn.restapi.data.repository.SharedLinkRepository;
import au.org.aodn.nrmn.restapi.service.upload.S3IO;
import au.org.aodn.nrmn.restapi.service.upload.SharedLinkService;

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

    @Autowired
    SharedLinkRepository sharedLinkRepository;

    @Autowired
    private SharedLinkService sharedLinkService;

    private final Integer pageSize = 50000;

    private final List<Pair<String, String>> countries = Arrays.asList(Pair.of("australia", "Australia"));

    private final List<Pair<String, String>> states = Arrays.asList(
            Pair.of("tas", "Tasmania"),
            Pair.of("nsw", "New South Wales"),
            Pair.of("vic", "Victoria"),
            Pair.of("qld", "Queensland"),
            Pair.of("sa", "South Australia"),
            Pair.of("wa", "Western Australia"),
            Pair.of("nt", "Northern Territory"));

    private void uploadMaterializedView(String viewName, List<Pair<String, String>> countries,
            List<Pair<String, String>> states,
            Long count, BiFunction<Integer, Integer, List<Tuple>> getFunction) throws IOException {

        logger.info("Generating CSV extracts for view " + viewName);

        countries = countries == null ? Arrays.asList() : countries;
        states = states == null ? Arrays.asList() : states;

        var offset = 0;
        var viewResult = getFunction.apply(offset, pageSize);
        offset += pageSize;
        var headers = viewResult.get(0).getElements().stream().map(e -> e.getAlias())
                .collect(Collectors.toUnmodifiableList());
        var initialValues = viewResult.stream().map(e -> e.toArray()).collect(Collectors.toUnmodifiableList());
        var countryIndex = headers.indexOf("country");
        var stateIndex = headers.indexOf("area");
        var requests = Stream.concat(
                states.stream()
                        .map(state -> new CSVFilterPrinter(headers, viewName + "_" + state.getLeft(), stateIndex,
                                state.getRight())),
                countries.stream()
                        .map(country -> new CSVFilterPrinter(headers, viewName + "_" + country.getLeft(), countryIndex,
                                country.getRight())))
                .collect(Collectors.toList());

        requests.add(new CSVFilterPrinter(headers, viewName, null, null));

        for (var request : requests) {
            logger.info("Writing CSV  " + request.getViewName());
            request.writeOut(initialValues);
        }


        while (offset < count) {
            var nextValues = getFunction.apply(offset, pageSize).stream()
                    .map(e -> e.toArray())
                    .collect(Collectors.toUnmodifiableList());
            offset += pageSize;
            for (var request : requests)
                request.writeOut(nextValues);
        }

        for (var request : requests) {
            logger.info("Uploading CSV " + request.getViewName());
            s3IO.uploadEndpoint(request.getViewName(), request.getFile());
            request.close();
        }
    }

    public void expireMaterializedViews() {
        var now = LocalDateTime.now();
        for (var link : sharedLinkRepository.findAll()) {
            try {
                if (link.getExpires().isBefore(now))
                    sharedLinkService.expireLink(link);
            } catch (Exception e) {
                logger.error("Failed to expire endpoint", e);
            }
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

            uploadMaterializedView("ep_site_list", null, null,
                    materializedViewsRepository.countEpSiteList(),
                    materializedViewsRepository::getEpSiteList);

            uploadMaterializedView("ep_m1", countries, states,
                    materializedViewsRepository.countEpM1(),
                    materializedViewsRepository::getEpM1);

            uploadMaterializedView("ep_m2_cryptic_fish", countries, states,
                    materializedViewsRepository.countEpM2CrypticFish(),
                    materializedViewsRepository::getEpM2CrypticFish);

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

        uploadAllMaterializedViews();
    }
}
