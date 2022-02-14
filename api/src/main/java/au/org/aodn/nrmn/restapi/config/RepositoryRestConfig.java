package au.org.aodn.nrmn.restapi.config;

import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Component
public class RepositoryRestConfig implements RepositoryRestConfigurer {

    @Value("${app.cors.max_age_secs}")
    private long MAX_AGE_SECS;

    @Autowired
    private EntityManager entityManager;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        /* expose all id's */
        config.exposeIdsFor(entityManager.getMetamodel()
            .getEntities()
            .stream()
            .map(e -> e.getJavaType())
            .collect(Collectors.toList())
            .toArray(new Class[0]));
            
        /* disable default exposure - must annotate methods to be exposed */
        config.disableDefaultExposure();
        
        /* configure CORS for spring data rest */
        cors.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
            .maxAge(MAX_AGE_SECS);
    }
} 
