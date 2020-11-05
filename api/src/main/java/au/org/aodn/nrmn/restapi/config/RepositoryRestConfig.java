package au.org.aodn.nrmn.restapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.stream.Collectors;

@Component
public class RepositoryRestConfig extends RepositoryRestConfigurerAdapter {

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
    }
} 