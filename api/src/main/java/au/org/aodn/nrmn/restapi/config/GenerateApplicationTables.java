package au.org.aodn.nrmn.restapi.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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

        String[] profiles = this.environment.getActiveProfiles();
        logger.info("profiles found : {}", Arrays.toString(profiles));
    }
}
