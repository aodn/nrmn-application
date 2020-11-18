package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.StagedJobLog.StagedJobLogBuilder;
import au.org.aodn.nrmn.restapi.model.db.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.repository.StagedJobLogRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StagedJobLogTestData {

    @Autowired
    private StagedJobTestData stagedJobTestData;

    @Autowired
    private StagedJobLogRepository stagedJobLogRepository;

    public StagedJobLog persistedStagedJobLog() {
        val stagedJobLog = defaultBuilder().build();
       return stagedJobLogRepository.saveAndFlush(stagedJobLog);
    }

    public StagedJobLogBuilder defaultBuilder() {
        return StagedJobLog.builder()
            .eventType(StagedJobEventType.INGESTING)
            .stagedJob(stagedJobTestData.persistedStagedJob())
            .details("More information about ingestion");
    }

}
