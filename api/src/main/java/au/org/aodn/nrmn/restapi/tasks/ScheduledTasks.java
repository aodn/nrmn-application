package au.org.aodn.nrmn.restapi.tasks;

import au.org.aodn.nrmn.restapi.service.PQSurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ScheduledTasks {

    @Autowired
    private PQSurveyService pqSurveyService;
    
    @Autowired
    private Environment environment;

    private static Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Scheduled(cron = "0 0 0 * * ?", zone = "Australia/Sydney")
    public void updateSurveyPQFields() {
        if(environment.getActiveProfiles().length < 1)
            pqSurveyService.updatePQSurveyFlags();
        else
            logger.info("Skipping PQ update as active profile set.");

    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        updateSurveyPQFields();
    }
}
