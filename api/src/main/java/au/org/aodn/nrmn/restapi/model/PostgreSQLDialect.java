package au.org.aodn.nrmn.restapi.model;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL10Dialect;

public class PostgreSQLDialect extends PostgreSQL10Dialect {
    public PostgreSQLDialect() {
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}
