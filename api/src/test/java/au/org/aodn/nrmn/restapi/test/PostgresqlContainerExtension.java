package au.org.aodn.nrmn.restapi.test;

import org.junit.jupiter.api.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

/* Adds a persistent postgres container for use in tests
 * Only started if running a test that references it and then only shut down
 * when the jvm is shut down so it can be reused in multiple tests */

public class PostgresqlContainerExtension implements Extension {

    private static final Logger logger = LoggerFactory.getLogger(PostgresqlContainerExtension.class);
    
    private static final DockerImageName POSTGIS_IMAGE_NAME = DockerImageName.parse("mdillon/postgis:10").asCompatibleSubstituteFor("postgres");

    @Container
    static public PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGIS_IMAGE_NAME);

    static {
        try {
            container.start();
            System.setProperty("DB_URL", container.getJdbcUrl());
            System.setProperty("DB_USERNAME", container.getUsername());
            System.setProperty("DB_PASSWORD", container.getPassword());
        } catch (Throwable t) {
            logger.error("Couldn't start postgis container", t);
        }
    }
}
