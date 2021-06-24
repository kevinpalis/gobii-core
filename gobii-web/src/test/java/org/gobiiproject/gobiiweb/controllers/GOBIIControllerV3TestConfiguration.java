package org.gobiiproject.gobiiweb.controllers;

import org.gobiiproject.gobiiweb.DataSourceSelector;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("projectsController-test")
@Configuration
public class GOBIIControllerV3TestConfiguration {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GOBIIControllerV3TestConfiguration .class);

    @Bean
    @Primary
    public DataSourceSelector dataSourceMulti() {
        LOGGER.debug("Test DataSourceSelector set");
        return Mockito.mock(DataSourceSelector.class);
    }

}