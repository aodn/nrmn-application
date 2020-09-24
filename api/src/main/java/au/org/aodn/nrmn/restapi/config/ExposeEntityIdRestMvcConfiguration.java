package au.org.aodn.nrmn.restapi.config;

import au.org.aodn.nrmn.restapi.model.db.DiverRefEntity;
import au.org.aodn.nrmn.restapi.model.db.SecRoleEntity;
import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.*;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.stereotype.Component;

import java.awt.print.Book;

@Component
public class ExposeEntityIdRestMvcConfiguration extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(SecRoleEntity.class);
        config.exposeIdsFor(DiverRefEntity.class);
        config.exposeIdsFor(SecUserEntity.class);


    }
}