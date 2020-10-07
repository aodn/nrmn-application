package au.org.aodn.nrmn.restapi.security;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import lombok.val;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Testcontainers
@SpringBootTest
@ActiveProfiles("cicd")
class JwtTokenProviderIT {

    @Autowired
    JwtTokenProvider provider;

    @Container
    public PostgreSQLContainer db = new PostgreSQLContainer("mdillon/postgis:9.6");

    @Test
    @Sql({"/testdata/FILL_ROLES.sql", "/testdata/FILL_USER.sql"})
    @WithUserDetails("tj@gmail.com")
    public void GenerateToken() {

        val auth = getContext().getAuthentication();
        val token = provider.generateToken(auth);
        assertTrue(token.length() > 20);
        assertEquals(provider.getUserIdFromJWT(token), 123456);

    }

    @AfterAll
    public void cleanUp() {
        ScriptUtils.runInitScript(new JdbcDatabaseDelegate(db, ""), "/testdata/DROP_NRMN.sql");
    }
}