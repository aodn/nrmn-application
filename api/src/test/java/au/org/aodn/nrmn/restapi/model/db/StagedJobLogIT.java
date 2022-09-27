package au.org.aodn.nrmn.restapi.model.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import au.org.aodn.nrmn.db.model.StagedJobLog;
import au.org.aodn.nrmn.db.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;

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
        LocalDateTime startTime = LocalDateTime.now();
        StagedJobLog stagedJobLog = stagedJobLogTestData.persistedStagedJobLog();
        entityManager.clear();

        StagedJobLog retrievedStagedJobLog = stagedJobLogRepository.findById(stagedJobLog.getId()).get();
        assertEquals(stagedJobLog.toString(), retrievedStagedJobLog.toString());
        assertThat(retrievedStagedJobLog.getEventTime().toLocalDateTime(),
            is(both(greaterThanOrEqualTo(startTime)).and(lessThanOrEqualTo(LocalDateTime.now()))));
    }
}
