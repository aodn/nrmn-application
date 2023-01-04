package au.org.aodn.nrmn.restapi.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private PQSurveyService pqSurveyService;

    @Autowired
    private Environment environment;

    @Autowired
    private MaterializedViewService materializedViewService;
    
    private static Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @PostConstruct
    public void onStartup() {
        performDailyTasks();
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Australia/Sydney")
    public void performDailyTasks() {
        if (environment.getActiveProfiles().length < 1) {
            pqSurveyService.updatePQSurveyFlags();
            materializedViewService.refreshAllMaterializedViews();
            materializedViewService.expireMaterializedViews();
        } else {
            logger.info("Skipping PQ update as active profile set.");
        }
    }
}
