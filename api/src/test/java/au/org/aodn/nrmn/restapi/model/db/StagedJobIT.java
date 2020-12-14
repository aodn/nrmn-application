package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
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
class StagedJobIT {

    @Autowired
    private StagedJobRepository stagedJobRepository;

    @Autowired
    private StagedJobTestData stagedJobTestData;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        val startTime = LocalDateTime.now();
        val stagedJob = stagedJobTestData.persistedStagedJob();
        entityManager.clear();
        val retrievedStagedJob = stagedJobRepository.findById(stagedJob.getId()).get();
        assertEquals(stagedJob, retrievedStagedJob);
        assertThat(retrievedStagedJob.getCreated().toLocalDateTime(),
                is(both(greaterThanOrEqualTo(startTime)).and(lessThanOrEqualTo(LocalDateTime.now()))));
        assertThat(retrievedStagedJob.getLastUpdated().toLocalDateTime(),
                is(both(greaterThanOrEqualTo(startTime)).and(lessThanOrEqualTo(LocalDateTime.now()))));
    }

    @Test
    public void testLastUpdated() {
        val stagedJob = stagedJobTestData.persistedStagedJob();
        stagedJob.setStatus(StatusJobType.FAILED);
        entityManager.clear();
        stagedJobRepository.saveAndFlush(stagedJob);
        val retrievedStagedJob = stagedJobRepository.findById(stagedJob.getId()).get();
        assertThat(retrievedStagedJob.getLastUpdated().toLocalDateTime(),
                is(both(greaterThanOrEqualTo(stagedJob.getCreated().toLocalDateTime())).and(lessThanOrEqualTo(LocalDateTime.now()))));
    }
}
