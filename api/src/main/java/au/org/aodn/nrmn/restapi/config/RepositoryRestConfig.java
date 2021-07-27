package au.org.aodn.nrmn.restapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.stream.Collectors;

@Component
public class RepositoryRestConfig implements RepositoryRestConfigurer {

    @Value("${app.cors.max_age_secs}")
    private long MAX_AGE_SECS;

    @Autowired
    private EntityManager entityManager;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
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
        config.getCorsRegistry().addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
            .maxAge(MAX_AGE_SECS);
    }
    
    @Bean
    public HateoasPageableHandlerMethodArgumentResolver customResolver(
        /* default to all records for listing reference data if paging parameters not specified */
        HateoasPageableHandlerMethodArgumentResolver pageableResolver) {
        pageableResolver.setOneIndexedParameters(true);
        pageableResolver.setFallbackPageable(PageRequest.of(0, Integer.MAX_VALUE));
        pageableResolver.setMaxPageSize(Integer.MAX_VALUE);
        return pageableResolver;
    }
} 
