package au.org.aodn.nrmn.restapi.test.annotations;

import org.springframework.test.context.jdbc.Sql;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Composed annotation that clears schema and loads test data before each test
 */
@Sql({"/sql/drop_nrmn.sql", "/sql/migration.sql", "/sql/application.sql", "/testdata/TEST_USER.sql",
        "/testdata/FILL_DATA.sql"})
@Retention(RUNTIME)
@Target(TYPE)
public @interface WithTestData {
}
