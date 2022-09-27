package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.db.model.StagedJobLog;
import au.org.aodn.nrmn.db.model.StagedJobLog.StagedJobLogBuilder;
import au.org.aodn.nrmn.db.model.enums.StagedJobEventType;
import au.org.aodn.nrmn.db.repository.StagedJobLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StagedJobLogTestData {

    @Autowired
    private StagedJobTestData stagedJobTestData;

    @Autowired
    private StagedJobLogRepository stagedJobLogRepository;

    public StagedJobLog persistedStagedJobLog() {
        StagedJobLog stagedJobLog = defaultBuilder().build();
       return stagedJobLogRepository.saveAndFlush(stagedJobLog);
    }

    public StagedJobLogBuilder defaultBuilder() {
        return StagedJobLog.builder()
            .eventType(StagedJobEventType.INGESTING)
            .stagedJob(stagedJobTestData.persistedStagedJob())
            .details("More information about ingestion");
    }

}
