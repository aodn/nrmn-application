package au.org.aodn.nrmn.restapi.config;

import au.org.aodn.nrmn.restapi.model.db.*;

import org.springframework.data.rest.core.config.*;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Component;

@Component
public class ExposeEntityIdRestMvcConfiguration extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(SecRole.class);
        config.exposeIdsFor(Diver.class);
        config.exposeIdsFor(SecUser.class);
        config.exposeIdsFor(Location.class);
        config.exposeIdsFor(Site.class);
        config.exposeIdsFor(Program.class);
    }
}