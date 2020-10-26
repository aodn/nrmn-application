package au.org.aodn.nrmn.restapi.model.db;

import org.hibernate.dialect.PostgreSQL10Dialect;

import java.sql.Types;

public class PostgreSQLDialect extends PostgreSQL10Dialect {
    public PostgreSQLDialect() {
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}
