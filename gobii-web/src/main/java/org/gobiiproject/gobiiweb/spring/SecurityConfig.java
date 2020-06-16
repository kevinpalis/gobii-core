package org.gobiiproject.gobiiweb.spring;

//import org.gobiiproject.gobidomain.security.TokenManager;
//import org.gobiiproject.gobidomain.security.impl.TokenManagerSingle;
import org.gobiiproject.gobidomain.services.AuthenticationService;
// import org.gobiiproject.gobidomain.services.ContactService;
// import org.gobiiproject.gobidomain.services.impl.AuthenticationServiceDefault;
// import org.gobiiproject.gobidomain.services.impl.UserDetailsServiceImpl;
// import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
// import org.gobiiproject.gobiimodel.config.ConfigSettings;
// import org.gobiiproject.gobiimodel.config.RestResourceId;
// import org.gobiiproject.gobiimodel.types.GobiiAuthenticationType;
// import org.gobiiproject.gobiiweb.security.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.builders.WebSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.session.SessionRegistryImpl;
// import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
// import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
// import org.springframework.web.filter.GenericFilterBean;

import lombok.extern.slf4j.Slf4j;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;

@KeycloakConfiguration
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter
{
    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
        //RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    

}

// /**
//  * Created by Phil on 3/22/2017.
//  */

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig extends WebSecurityConfigurerAdapter {

//     @Autowired
//     private ContactService contactService;

//     private static String CONFIG_FILE_LOCATION_PROP = "cfgFqpn";

//     private static ConfigSettings CONFIG_SETTINGS;

//     public SecurityConfig() {
//         String configFileLocation =
//                 System.getProperty(CONFIG_FILE_LOCATION_PROP);
//         SecurityConfig.CONFIG_SETTINGS = new ConfigSettings(configFileLocation);
//     }

//     @Override
//     @Bean(name = "restAuthenticationManager")
//     public AuthenticationManager authenticationManagerBean() throws Exception {

//         AuthenticationManager returnVal = super.authenticationManagerBean();
//         return returnVal;
//     }


//     // allow requests for static pages
//     // see http://stackoverflow.com/questions/
//     // 30366405/how-to-disable-spring-security-for-particular-url
//     @Override
//     public void configure(WebSecurity web) throws Exception {

//         String configSettingsUrl =
//                 RestResourceId.GOBII_CONFIGSETTINGS.getRequestUrl(
//                         null, GobiiControllerType.GOBII.getControllerPath());

//         web.ignoring().antMatchers(configSettingsUrl,
//                 "/login",
//                 "/index.html",
//                 "/css/**",
//                 "/images/**",
//                 "/js/**"
//              //   ,"/docs/**"
//         );

//         if (!CONFIG_SETTINGS.isAuthenticateBrapi()) {

//             String allBrapiUrls =
//                     GobiiControllerType.BRAPI.getControllerPath() + "**";

//             String gobiiFIlesUrl =
//                     RestResourceId.GOBII_FILES.getRequestUrl(
//                             null, GobiiControllerType.GOBII.getControllerPath())
//                             + "/**";

//             web.ignoring().antMatchers(allBrapiUrls, gobiiFIlesUrl);

//         }

//         // the calling the calls call does not require authentication
//         String brapiCallsUrl =
//                 RestResourceId.BRAPI_CALLS.getRequestUrl(
//                         null,
//                         GobiiControllerType.BRAPI.getControllerPath());

//         String brapiServerInfoUrl =
//                 RestResourceId.BRAPI_SERVER_INFO.getRequestUrl(
//                         null,
//                         GobiiControllerType.BRAPI_V2.getControllerPath()) +
//                         "/**";

//         web.ignoring()
//                 .antMatchers(brapiCallsUrl)
//                 .antMatchers(brapiServerInfoUrl);

//         web.ignoring().antMatchers(HttpMethod.OPTIONS);

//     }


//     @Override
//     protected void configure(HttpSecurity http) throws Exception {

//         // intellij complains about
//         // the derivation of BasicAuthenticationFilter.class,
//         // but it is WRONG; this works fine
//         String allGobiimethods =
//                 GobiiControllerType.GOBII.getControllerPath() + "/**";


//         http.addFilterAfter(this.filterBean(), BasicAuthenticationFilter.class);

//         http
//                 .csrf().disable()
//                 .sessionManagement()
//                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                 .and()
//                 .authorizeRequests()
//                 .antMatchers(allGobiimethods).permitAll()
//                 .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
//                 .anyRequest().authenticated()
//                 .and()
//                 .anonymous().disable();

//     }

//     // for debug output from open ldap, execute: C:\OpenLDAP\slapd.exe -d -1

//     // in the server configuration, the ldap url _must_ be fully qualified with the path to the group
//     // that contains the user (e.g., ldap://localhost:389/ou=People,dc=maxcrc,dc=com); I have tried
//     // numerous ways to configure the group path in Java but there does not appear to be a way to do this;
//     // In xml configuration you set the base property of context-source, but that does not appear to be an
//     // option here.
//     @Override
//     protected void
//     configure(AuthenticationManagerBuilder authenticationManagerBuilder)
//             throws Exception {

//         String configFileLocation =
//                 System.getProperty(CONFIG_FILE_LOCATION_PROP);
//         ConfigSettings configSettings = new ConfigSettings(configFileLocation);
//         GobiiAuthenticationType gobiiAuthenticationType =
//                 configSettings.getGobiiAuthenticationType();
//         if (gobiiAuthenticationType.equals(GobiiAuthenticationType.TEST)) {

//             authenticationManagerBuilder
//                     .userDetailsService(this.userDetailService());

//         } else if (gobiiAuthenticationType.equals(GobiiAuthenticationType.KEYCLOAK)) {
        
//         } else {

//             String dnPattern = configSettings.getLdapUserDnPattern();
//             String managerUser = configSettings.getLdapBindUser();
//             String managerPassword = configSettings.getLdapBindPassword();
//             String url = configSettings.getLdapUrl();

//             if (gobiiAuthenticationType.equals(
//                     GobiiAuthenticationType.LDAP_CONNECT_WITH_MANAGER)) {

//                 authenticationManagerBuilder
//                         .ldapAuthentication()
//                         .userSearchFilter(dnPattern)
//                         .contextSource()
//                         .managerDn(managerUser)
//                         .managerPassword(managerPassword)
//                         .url(url);

//             } else if (
//                     gobiiAuthenticationType
//                             .equals(GobiiAuthenticationType.LDAP)) {

//                 authenticationManagerBuilder
//                         .ldapAuthentication()
//                         .userSearchFilter(dnPattern)
//                         .contextSource()
//                         .url(url);
//             }
//         }
//     } // configure()


//     @Bean(name = "userDetailService")
//     UserDetailsService userDetailService() {
//         return new UserDetailsServiceImpl();
//     }

//     @Bean(name = "tokenManager")
//     public TokenManager tokenManager() {
//         return new TokenManagerSingle();
//     }

//     @Bean(name = "authenticationService")
//     public AuthenticationService authenticationService() throws Exception {
//         return new AuthenticationServiceDefault(this.authenticationManager(),
//                 this.tokenManager());
//     }

//     @Bean(name = "restAuthenticationFilter")
//     public GenericFilterBean filterBean() throws Exception {
//         return new TokenAuthenticationFilter(this.authenticationService(),
//                 "",
//                 this.contactService);
//     }

// }

