package au.org.aodn.nrmn.restapi.config;

import au.org.aodn.nrmn.restapi.model.db.*;

import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAudit;
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

import java.sql.Timestamp;
import java.util.*;


@Component
public class GenerateApplicationTables implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String pwd;
    @Autowired
    private Environment environment;
    static Logger logger = LoggerFactory.getLogger(GenerateApplicationTables.class);

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        val profiles = this.environment.getActiveProfiles();
        logger.info("profiles found : {}", Arrays.toString(profiles));
        if (false && Arrays.stream(profiles).anyMatch("local"::equals)) {
            Map<String, String> settings = new HashMap<>();
            settings.put("dialect", "org.hibernate.dialect.PostgreSQLDialect");
            settings.put("hibernate.connection.url", url);
            settings.put("hibernate.connection.username", username);
            settings.put("hibernate.connection.password", pwd);
            settings.put("hibernate.hbm2ddl.auto", "create");
            settings.put("hibernate.default_schema", "nrmn");
            settings.put("hibernate.default_catalog", "nrmn");
            settings.put("show_sql", "true");

            _generatingTables(settings, Arrays.asList(SecRole.class, SecUser.class), "createAuthTables");
            _generatingTables(
                    settings,
                    Arrays.asList(
                            Diver.class,
                            UserActionAudit.class,
                            ObservableItem.class,
                            Observation.class,
                            Survey.class,
                            SurveyMethod.class,
                            Site.class,
                            Location.class
                    ),
                    "createAuditTable");
            _generatingTables(
                    settings,
                    Arrays.asList(
                            StagedRowError.class,
                            StagedJob.class,
                            StagedRow.class),
                    "createIngestTables"
            );


            _generatingTables(
                    settings,
                    Arrays.asList(
                            Program.class,
                            Site.class),
                    "createExcludeDataTables"
            );

        }
    }

    private void _generatingTables(Map<String, String> settings, List<Class> entities, String sqlFileName) {
        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder().applySettings(settings).build()
        );
        entities.forEach(c -> metadata.addAnnotatedClass(c));
        val schemaExport = new SchemaExport();
        schemaExport.setFormat(true);
        schemaExport.setDelimiter(";");
        schemaExport.setHaltOnError(true);
        val timestamp = new Timestamp(System.currentTimeMillis());

        schemaExport.setOutputFile("sql/" + sqlFileName + "." + timestamp.getTime() + ".sql");
        schemaExport.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.BOTH, metadata.buildMetadata());
    }
}