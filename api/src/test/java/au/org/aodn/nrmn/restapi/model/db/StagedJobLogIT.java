package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
    pattern = ".*TestData"))
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
class StagedJobLogIT {

    @Autowired
    private StagedJobLogRepository stagedJobLogRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StagedJobLogTestData stagedJobLogTestData;

    @Test
    public void testMapping() {
        val startTime = LocalDateTime.now();
        val stagedJobLog = stagedJobLogTestData.persistedStagedJobLog();
        entityManager.clear();

        val retrievedStagedJobLog = stagedJobLogRepository.findById(stagedJobLog.getId()).get();
        assertEquals(stagedJobLog, retrievedStagedJobLog);
        assertThat(retrievedStagedJobLog.getEventTime().toLocalDateTime(),
            is(both(greaterThanOrEqualTo(startTime)).and(lessThanOrEqualTo(LocalDateTime.now()))));
    }
}
