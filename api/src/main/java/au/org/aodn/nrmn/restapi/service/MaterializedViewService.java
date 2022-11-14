package au.org.aodn.nrmn.restapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import au.org.aodn.nrmn.restapi.data.repository.MaterializedViewsRepository;

@Component
@EnableAsync
public class MaterializedViewService {

    private static final Logger logger = LoggerFactory.getLogger(MaterializedViewService.class);

    @Autowired
    private Environment environment;

    @Autowired
    MaterializedViewsRepository materializedViewsRepository;

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
