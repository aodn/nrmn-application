package au.org.aodn.nrmn.restapi.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.service.PQSurveyService;

@Component
public class ScheduledTasks {

    @Autowired
    private PQSurveyService pqSurveyService;

    @Autowired
    private Environment environment;

    private static Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Scheduled(cron = "0 0 0 * * ?", zone = "Australia/Sydney")
    public void updateSurveyPQFields() {
        if (environment.getActiveProfiles().length < 1)
            pqSurveyService.updatePQSurveyFlags();
        else
            logger.info("Skipping PQ update as active profile set.");

    }
}
