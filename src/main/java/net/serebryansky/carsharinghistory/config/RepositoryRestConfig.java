package net.serebryansky.carsharinghistory.config;

import net.serebryansky.carsharinghistory.domain.Event;
import net.serebryansky.carsharinghistory.domain.Photo;
import net.serebryansky.carsharinghistory.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@Configuration
public class RepositoryRestConfig extends RepositoryRestConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(RepositoryRestConfig.class);

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(Photo.class);
        config.exposeIdsFor(Event.class);
    }
}
