package au.org.aodn.nrmn.restapi.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WormsServiceTest {
    @ParameterizedTest
    @CsvSource(value = {
            "some text sp. (trailing)|some text",
            "some text spp. (trailing)|some text",
            "some text (trailing)|some text",
            "some text sp. [trailing]|some text",
            "some text spp. [trailing]|some text",
            "some text [trailing]|some text",
            "some text|some text"
    }, delimiter = '|')
    public void testRemoveJunkPattern(String value, String expectedResult) {
        String result = WormsService.removeTrailingJunk(value);
        assertThat(expectedResult, is(equalTo(result)));
    }
}
