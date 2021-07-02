package au.org.aodn.nrmn.restapi.tasks;

import au.org.aodn.nrmn.restapi.service.PQSurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private PQSurveyService pqSurveyService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateSurveyPQFields() {
        pqSurveyService.updatePQSurveyFlags();
    }
}
