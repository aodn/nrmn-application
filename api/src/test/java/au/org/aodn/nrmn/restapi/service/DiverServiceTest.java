package au.org.aodn.nrmn.restapi.service;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.org.aodn.nrmn.restapi.data.model.Diver;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import io.jsonwebtoken.lang.Assert;

@RunWith(MockitoJUnitRunner.class)
public class DiverServiceTest {

    @Mock
    DiverRepository diverRepository;

    @InjectMocks
    DiverService diverService;

    public static int OlderThan24HoursDiverId = 1;
    public static int LessThan24HoursDiverId = 2;
    public static int NullDiverId = 3;

    @Before
    public void setUp() {
        when(diverRepository.findById(1)).thenReturn(Optional.of(Diver.builder().diverId(1).created(LocalDateTime.now().minusHours(25)).build()));
        when(diverRepository.findById(2)).thenReturn(Optional.of(Diver.builder().diverId(2).created(LocalDateTime.now().minusHours(23)).build()));
        when(diverRepository.findById(3)).thenReturn(Optional.of(Diver.builder().diverId(3).created(null).build()));
    }

    @Test
    public void olderThan24HoursDiverDeleteFailsTest() {
        var error = diverService.deleteDiver(OlderThan24HoursDiverId);
        Assert.notNull(error);
    }

    @Test
    public void nullDiverDeleteFailsTest() {
        var error = diverService.deleteDiver(NullDiverId);
        Assert.notNull(error);
    }

    @Test
    public void lessThan24HoursDiverDeleteSucceedsTest() {
        var error = diverService.deleteDiver(LessThan24HoursDiverId);
        Assert.isNull(error);
    }
}