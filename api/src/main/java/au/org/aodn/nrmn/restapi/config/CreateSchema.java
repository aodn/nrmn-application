package au.org.aodn.nrmn.restapi.config;

import au.org.aodn.nrmn.restapi.model.db.UserSecEntity;
import au.org.aodn.nrmn.restapi.model.db.UserSecRoleEntity;
import lombok.val;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


@Component
public class CreateSchema implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String pwd;
    @Autowired
    private Environment environment;
   static Logger logger = LoggerFactory.getLogger(CreateSchema.class);
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        val profiles = this.environment.getActiveProfiles();
        logger.info("profiles found : {}", Arrays.toString(profiles));
        if (Arrays.stream(profiles).anyMatch("local"::equals)) {
            Map<String, String> settings = new HashMap<>();
            settings.put("dialect", "org.hibernate.dialect.PostgreSQLDialect");
            settings.put("hibernate.connection.url", url);
            settings.put("hibernate.connection.username", username);
            settings.put("hibernate.connection.password", pwd);
            settings.put("hibernate.hbm2ddl.auto", "create");
            settings.put("show_sql", "true");
            MetadataSources metadata = new MetadataSources(
                    new StandardServiceRegistryBuilder().applySettings(settings).build()
            );
            metadata.addAnnotatedClass(UserSecEntity.class);
            metadata.addAnnotatedClass(UserSecRoleEntity.class);
            SchemaExport schemaExport = new SchemaExport();
            schemaExport.setFormat(true);
            schemaExport.setDelimiter(";");
            schemaExport.setHaltOnError(true);
            schemaExport.setOutputFile("sql/createAuthTables.sql");
            schemaExport.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.BOTH, metadata.buildMetadata());
        }
    }
}