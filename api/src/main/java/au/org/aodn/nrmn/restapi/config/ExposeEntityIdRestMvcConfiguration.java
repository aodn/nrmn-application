package au.org.aodn.nrmn.restapi.config;

import au.org.aodn.nrmn.restapi.model.db.*;

import org.springframework.data.rest.core.config.*;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Component;

@Component
public class ExposeEntityIdRestMvcConfiguration extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(SecRoleEntity.class);
        config.exposeIdsFor(DiverRefEntity.class);
        config.exposeIdsFor(SecUserEntity.class);
        config.exposeIdsFor(LocationRefEntity.class);
        config.exposeIdsFor(SiteRefEntity.class);
        config.exposeIdsFor(ProgramRefEntity.class);
    }
}