package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.StagedJobLog;
import au.org.aodn.nrmn.restapi.data.model.StagedJobLog.StagedJobLogBuilder;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;

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
