package org.gobiiproject.gobiimodel.config;

import java.util.Map;

import org.simpleframework.xml.Element;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * { "realm": "Gobii", "resource": "gobii-project", "bearer-only": true,
 * "auth-server-url": "http://localhost:8181/auth", "ssl-required": "external",
 * "principal-attribute": "preferred_username" }
 */
@Data
@NoArgsConstructor
public class KeycloakConfig {

    @Element(required = false)
    private String realm;

    @Element(required = false)
    private String resource;

    @Element(required = false)
    private boolean bearerOnly = false;

    @Element(required = false)
    private String authServerUrl;

    @Element(required = false)
    private String sslRequired = "NONE" ;

    @Element(required = false)
    private String principalAttribute = "preferred_username";

    //----
    @Element(required = false)
    private String realmPublicKey;

    @Element(required = false)
    private int confidentialPort = 8443;

    @Element(required = false)
    private boolean useResourceRoleMappings = true; //default to true here

    @Element(required = false)
    private boolean publicClient = false;

    @Element(required = false)
    private boolean enableCors = false;

    @Element(required = false)
    private int corsMaxAge;

    @Element(required = false)
    private String corsAllowedMethods;

    @Element(required = false)
    private String corsAllowedHeaders;

    @Element(required = false)
    private String corsExposedHeaders;

    @Element(required = false)
    private boolean autodetectBearerOnly = false;

    @Element(required = false)
    private boolean enableBasicAuth = false;

    @Element(required = false)
    private boolean exposeToken = false;

    @Element(required = false)
    private String credentials;

    @Element(required = false)
    private int connectionPoolSize = 20;

    @Element(required = false)
    private boolean disableTrustManager = false;

    @Element(required = false)
    private boolean allowAnyHostname = false;

    @Element(required = false)
    private String proxyUrl;

    @Element(required = false)
    private String truststore;

    @Element(required = false)
    private String truststorePassword;

    @Element(required = false)
    private String clientKeystore;

    @Element(required = false)
    private String clientKeystorePassword;

    @Element(required = false)
    private String clientKeyPassword;

    @Element(required = false)
    private boolean alwaysRefreshToken = false;

    @Element(required = false)
    private boolean registerNodeAtStartup = false;

    @Element(required = false)
    private int registerNodePeriod;

    @Element(required = false)
    private String tokenStore = "SESSION";

    @Element(required = false)
    private String tokenCookiePath;

    @Element(required = false)
    private boolean turnOffChangeSessionIdOnLogin = false;

    @Element(required = false)
    private int tokenMinimumTimeToLive = 0;

    @Element(required = false)
    private int minTimeBetweenJwksRequests = 10;

    @Element(required = false)
    private boolean ignoreOauthQueryParameter = false;

    @Element(required = false)
    private Map<String, String> redirectRewriteRules;

    @Element(required = false)
    private int publicKeyCacheTtl = 86400;

    @Element(required = false)
    private boolean verifyTokenAudience = false;

    //admin username & password
    @Element(required = false)
    private String adminUsername;

    @Element(required = false)
    private String adminPassword;

    //Added for extractor UI config
    @Element(required = false)
    private String extractorUIClient;
}