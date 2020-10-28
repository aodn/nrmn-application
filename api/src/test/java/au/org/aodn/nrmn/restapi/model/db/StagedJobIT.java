package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
    pattern = ".*TestData"))
@AutoConfigureTestDatabase(replace = Replace.NONE)
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
            is(both(greaterThan(startTime)).and(lessThan(LocalDateTime.now()))));
        assertThat(retrievedStagedJob.getLastUpdated().toLocalDateTime(),
            is(both(greaterThan(startTime)).and(lessThan(LocalDateTime.now()))));
    }

    @Test
    public void testLastUpdated() {
        val stagedJob = stagedJobTestData.persistedStagedJob();
        stagedJob.setStatus(StatusJobType.FAILED);
        stagedJobRepository.saveAndFlush(stagedJob);
        entityManager.clear();
        val retrievedStagedJob = stagedJobRepository.findById(stagedJob.getId()).get();
        assertThat(retrievedStagedJob.getLastUpdated().toLocalDateTime(),
            is(both(greaterThan(stagedJob.getCreated().toLocalDateTime())).and(lessThan(LocalDateTime.now()))));
    }
}