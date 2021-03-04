package au.org.aodn.nrmn.restapi.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class TimeUtilsTest {

    private static final DateTimeFormatter SIMPLE_FORMAT = DateTimeFormatter.ofPattern("H:mm[:ss]");

    @ParameterizedTest
    @CsvSource(value = {
            "10:15|10:15",
            "22:15|22:15",
            "10:15 am|10:15",
            "10:15 pm|22:15",
            "10:15 AM|10:15",
            "10:15 PM|22:15",
            "10:15:23|10:15:23",
            "22:15:23|22:15:23",
            "10:15:23 am|10:15:23",
            "10:15:23 pm|22:15:23",
            "10:15:23 AM|10:15:23",
            "10:15:23 PM|22:15:23"
    }, delimiter = '|')
    public void shouldBeParsedCorrectly(String value, String expectedTime) {
        assertThat(TimeUtils.parseTime(value), is(equalTo(LocalTime.parse(expectedTime, SIMPLE_FORMAT))));
    }
}
