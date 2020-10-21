package au.org.aodn.nrmn.restapi.util.loan;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class InputStreamLenderTest {
    @Test
    public void testInputStreamLender() throws Exception {
        val res = InputStreamLender.lend(() ->
                        this.getClass().getClassLoader().getResourceAsStream("application.properties"),
                (input) -> {
                    assertNotNull(input);
                    return 8;
                });;
        assertTrue(res.isPresent());
        assertEquals(res.get(), 8);
    }

    @Test
    public void testInputStreamLenderClose() throws Exception {
        AtomicReference<InputStream> in =
                new AtomicReference<>(this.getClass().getClassLoader().getResourceAsStream("application.properties"));

        val res = InputStreamLender.lend(
                in::get,
                (input) -> {
                    assertNotNull(input);
                    return 8;
                });;
       val exception = assertThrows(IOException.class,() -> in.get().available());
       assertEquals(exception.getMessage(), "Stream closed");
    }

}