package au.org.aodn.nrmn.restapi.dto.site;

import au.org.aodn.nrmn.restapi.enums.Iirc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SiteGetDtoTest {
    private SiteGetDto siteGetDto;

    @BeforeEach
    void setUp() {
        siteGetDto = new SiteGetDto();
    }

    @Test
    void testGetLongitudeWhenNull() {
        assertNull(siteGetDto.getLongitude(), "Longitude should be null when not set");
    }

    @Test
    void testGetLongitudeWhenNotNull() throws NoSuchFieldException, IllegalAccessException {
        // Set up reflection to access private field
        Field longitudeField = SiteGetDto.class.getDeclaredField("longitude");
        longitudeField.setAccessible(true);
        longitudeField.set(siteGetDto, 123.456789);

        // Mock Iirc.FORMAT_DIGIT
        String formatDigit = Iirc.FORMAT_DIGIT;
        String formatted = String.format(formatDigit, 123.456789);
        Double expected = Double.valueOf(formatted);

        assertEquals(expected, siteGetDto.getLongitude(), "Longitude should be formatted correctly");
    }

    @Test
    void testGetLatitudeWhenNull() {
        assertNull(siteGetDto.getLatitude(), "Latitude should be null when not set");
    }

    @Test
    void testGetLatitudeWhenNotNull() throws NoSuchFieldException, IllegalAccessException {
        // Set up reflection to access private field
        Field latitudeField = SiteGetDto.class.getDeclaredField("latitude");
        latitudeField.setAccessible(true);
        latitudeField.set(siteGetDto, -23.456789);

        // Mock Iirc.FORMAT_DIGIT
        String formatDigit = Iirc.FORMAT_DIGIT;
        String formatted = String.format(formatDigit, -23.456789);
        Double expected = Double.valueOf(formatted);

        assertEquals(expected, siteGetDto.getLatitude(), "Latitude should be formatted correctly");
    }
}
