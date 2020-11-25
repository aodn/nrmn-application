package au.org.aodn.nrmn.restapi.test;

import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.containers.PostgreSQLContainer;

/* Adds a persistent postgres container for use in tests
 * Only started if running a test that references it and then only shut down 
 * when the jvm is shut down so it can be reused in multiple tests */

public class PostgresqlContainerExtension implements Extension {

    static {
        PostgreSQLContainer container = new PostgreSQLContainer("mdillon/postgis:9.6");
        container.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }
}
