package au.org.aodn.nrmn.restapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.db.repository.MaterializedViewsRepository;

@Component
@EnableAsync
public class MaterializedViewService {

    private static final Logger logger = LoggerFactory.getLogger(MaterializedViewService.class);

    @Autowired
    MaterializedViewsRepository materializedViewsRepository;

    @Async
    public void refreshAllMaterializedViews() {

        logger.info("Starting materialization of endpoint views");

        if(materializedViewsRepository.checkUiSpeciesAttributesRunning()) {
            logger.info("ui_species_attributes is already being refreshed");
        } else {
            logger.info("refreshing ui_species_attributes");
            materializedViewsRepository.refreshUiSpeciesAttributes();
            logger.info("ui_species_attributes refresh complete");
        }

        if(materializedViewsRepository.checkEpM2CrypticFishRunning()) {
            logger.info("ep_m2_cryptic_fish is already being refreshed");
        } else {
            logger.info("refreshing ep_m2_cryptic_fish");
            materializedViewsRepository.refreshEpM2CrypticFish();
            logger.info("ep_m2_cryptic_fish refresh complete");
        }

        if(materializedViewsRepository.checkEpM2InvertsRunning()) {
            logger.info("ep_m2_inverts is already being refreshed");
        } else {
            logger.info("refreshing ep_m2_inverts");
            materializedViewsRepository.refreshEpM2Inverts();
            logger.info("ep_m2_inverts refresh complete");
        }

        if(materializedViewsRepository.checkEpObservableItemsRunning()) {
            logger.info("ep_observable_items is already being refreshed");
        } else {
            logger.info("refreshing ep_observable_items");
            materializedViewsRepository.refreshEpObservableItems();
            logger.info("ep_observable_items refresh complete");
        }

        if(materializedViewsRepository.checkEpRarityAbundanceRunning()) {
            logger.info("ep_rarity_abundance is already being refreshed");
        } else {
            logger.info("refreshing ep_rarity_abundance");
            materializedViewsRepository.refreshEpRarityAbundance();
            logger.info("ep_rarity_abundance refresh complete");
        }

        if(materializedViewsRepository.checkEpRarityExtentsRunning()) {
            logger.info("ep_rarity_extents is already being refreshed");
        } else {
            logger.info("refreshing ep_rarity_extents");
            materializedViewsRepository.refreshEpRarityExtents();
            logger.info("ep_rarity_extents refresh complete");
        }

        if(materializedViewsRepository.checkEpRarityRangeRunning()) {
            logger.info("ep_rarity_range is already being refreshed");
        } else {
            logger.info("refreshing ep_rarity_range");
            materializedViewsRepository.refreshEpRarityRange();
            logger.info("ep_rarity_range refresh complete");
        }

        if(materializedViewsRepository.checkEpSiteListRunning()) {
            logger.info("ep_site_list is already being refreshed");
        } else {
            logger.info("refreshing ep_site_list");
            materializedViewsRepository.refreshEpSiteList();
            logger.info("ep_site_list refresh complete");
        }

        if(materializedViewsRepository.checkEpSurveyListRunning()) {
            logger.info("ep_survey_list is already being refreshed");
        } else {
            logger.info("refreshing ep_survey_list");
            materializedViewsRepository.refreshEpSurveyList();
            logger.info("ep_survey_list refresh complete");
        }

        if(materializedViewsRepository.checkEpM1Running()) {
            logger.info("ep_m1 is already being refreshed");
        } else {
            logger.info("refreshing ep_m1");
            materializedViewsRepository.refreshEpM1();
            logger.info("ep_m1 refresh complete");
        }

        if(materializedViewsRepository.checkEpRarityFrequencyRunning()) {
            logger.info("ep_rarity_frequency is already being refreshed");
        } else {
            logger.info("refreshing ep_rarity_frequency");
            materializedViewsRepository.refreshEpRarityFrequency();
            logger.info("ep_rarity_frequency refresh complete");
        }

        logger.info("Re-materialization complete");

    }
}
