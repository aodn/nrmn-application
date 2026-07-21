package au.org.aodn.nrmn.restapi.service;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import au.org.aodn.nrmn.restapi.data.repository.PublicViewsRepository;
import au.org.aodn.nrmn.restapi.service.upload.S3IO;

@Service
public class PublicViewService {

    private static final Logger logger = LoggerFactory.getLogger(PublicViewService.class);

    private final Integer pageSize = 50000;

    @Autowired
    private S3IO s3IO;

    @Autowired
    private PublicViewsRepository publicViewsRepository;

    @Async
    public void publishPublicViewsAsync() {
        publishPublicViews();
    }

    public void publishPublicViews() {
        logger.info("Publishing public views to imos-data");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        publishView("ep_m0_off_transect_sighting_public",
                publicViewsRepository::countEpM0OffTransectSightingPublic,
                publicViewsRepository::getEpM0OffTransectSightingPublic);

        publishView("ep_m1_public",
                publicViewsRepository::countEpM1Public,
                publicViewsRepository::getEpM1Public);

        publishView("ep_m2_cryptic_fish_public",
                publicViewsRepository::countEpM2CrypticFishPublic,
                publicViewsRepository::getEpM2CrypticFishPublic);

        publishView("ep_m2_inverts_public",
                publicViewsRepository::countEpM2InvertsPublic,
                publicViewsRepository::getEpM2InvertsPublic);

        publishView("ep_m3_isq_public",
                publicViewsRepository::countEpM3IsqPublic,
                publicViewsRepository::getEpM3IsqPublic);

        publishView("ep_site_list_public",
                publicViewsRepository::countEpSiteListPublic,
                publicViewsRepository::getEpSiteListPublic);

        publishView("ep_survey_list_public",
                publicViewsRepository::countEpSurveyListPublic,
                publicViewsRepository::getEpSurveyListPublic);

        stopWatch.stop();
        logger.info("Published all public views in " + stopWatch.getLastTaskTimeMillis() + "ms");
    }

    private void publishView(String viewName, Supplier<Long> countFunction,
            BiFunction<Integer, Integer, List<Tuple>> getFunction) {
        try {
            uploadPublicView(viewName, countFunction.get(), getFunction);
        } catch (Exception e) {
            logger.error("Failed to publish public view " + viewName, e);
        }
    }

    private void uploadPublicView(String viewName, Long count,
            BiFunction<Integer, Integer, List<Tuple>> getFunction) {

        logger.info("Generating CSV extract for public view " + viewName);

        var offset = 0;
        var viewResult = getFunction.apply(offset, pageSize);
        offset += pageSize;
        // ignore the file generation if no rows (keep the last version)
        if (viewResult.isEmpty()) {
            logger.warn("No rows returned for view " + viewName + ", skipping CSV generation");
            return;
        }

        var headers = viewResult.get(0).getElements().stream().map(e -> e.getAlias())
                .collect(Collectors.toUnmodifiableList());
        var initialValues = viewResult.stream().map(e -> e.toArray()).collect(Collectors.toUnmodifiableList());

        // as requested, the file naming follows "relation_data.csv" such as "ep_m0_off_transect_sighting_public_data.csv"
        var request = new CSVFilterPrinter(headers, viewName + "_data", null, null);

        logger.info("Writing CSV  " + request.getViewName() + ".csv");
        request.writeOut(initialValues);

        while (offset < count) {
            var nextValues = getFunction.apply(offset, pageSize).stream()
                    .map(e -> e.toArray())
                    .collect(Collectors.toUnmodifiableList());
            offset += pageSize;
            request.writeOut(nextValues);
        }

        logger.info("Uploading CSV " + request.getViewName() + ".csv");
        s3IO.uploadPublicView(request.getViewName(), request.getFile());
        request.close();
    }
}
