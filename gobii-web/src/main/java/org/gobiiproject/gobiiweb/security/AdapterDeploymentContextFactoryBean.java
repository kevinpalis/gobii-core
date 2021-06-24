package org.gobiiproject.gobiiweb.security;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.gobiiproject.gobiimodel.config.KeycloakConfig;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.representations.adapters.config.AdapterConfig;

public class AdapterDeploymentContextFactoryBean implements FactoryBean<AdapterDeploymentContext>, InitializingBean {


    @Autowired
    private KeycloakConfig keycloakConfig;
    
    private AdapterDeploymentContext adapterDeploymentContext;

    public AdapterDeploymentContextFactoryBean() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        adapterDeploymentContext = new AdapterDeploymentContext( getKeycloakDeployment() );
    }

    @Override
    public AdapterDeploymentContext getObject() throws Exception {
        return adapterDeploymentContext;
    }

    @Override
    public Class<?> getObjectType() {
        return AdapterDeploymentContext.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private KeycloakDeployment getKeycloakDeployment() {
        AdapterConfig adapterConfig = new AdapterConfig();
        //minimum working attributes
        adapterConfig.setAuthServerUrl(keycloakConfig.getAuthServerUrl());
        
        adapterConfig.setRealm(keycloakConfig.getRealm());

        adapterConfig.setResource(keycloakConfig.getResource());
        adapterConfig.setSslRequired(keycloakConfig.getSslRequired());
        adapterConfig.setBearerOnly(keycloakConfig.isBearerOnly());
        adapterConfig.setPrincipalAttribute(keycloakConfig.getPrincipalAttribute());
        //TODO:  do a bean attribute copy

        KeycloakDeployment deployment =   KeycloakDeploymentBuilder.build(adapterConfig);

        return deployment;
        
    }

}