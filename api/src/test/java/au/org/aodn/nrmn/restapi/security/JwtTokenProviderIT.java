package au.org.aodn.nrmn.restapi.security;

import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Testcontainers
@SpringBootTest
@ActiveProfiles("cicd")
@ExtendWith(PostgresqlContainerExtension.class)
class JwtTokenProviderIT {

    @Autowired
    JwtTokenProvider provider;

    @Test
    @WithUserDetails("test@gmail.com")
    public void GenerateToken() {

        val auth = getContext().getAuthentication();
        val token = provider.generateToken(auth);
        assertTrue(token.length() > 20);
        assertEquals(provider.getUserIdFromJWT(token), 123456);

    }
}
