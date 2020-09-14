// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiidomain.services.impl;

import javax.annotation.PostConstruct;
import org.gobiiproject.gobiidomain.security.TokenInfo;
import org.gobiiproject.gobiidomain.security.TokenManager;
import org.gobiiproject.gobiidomain.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service responsible for all around authentication, token checks, etc.
 * This class does not care about HTTP protocol at all.
 */
public class AuthenticationServiceDefault implements AuthenticationService {

    @Autowired
    private ApplicationContext applicationContext;

    Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceDefault.class);

    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;

    public AuthenticationServiceDefault(AuthenticationManager authenticationManager, TokenManager tokenManager) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
    }

    @PostConstruct
    public void init() {
        System.out.println(" *** AuthenticationServiceImpl.init with: " + applicationContext);
    }

    @Override
    public TokenInfo authenticate(String login, String password) {

        TokenInfo returnVal = null;

        // Here principal=username, credentials=password
        Authentication authentication = new UsernamePasswordAuthenticationToken(login, password);

        try {
            authentication = authenticationManager.authenticate(authentication);
            // Here principal=UserDetails (UserContext in our case), credentials=null (security reasons)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.getPrincipal() != null) {

                UserDetails userContext = (UserDetails) authentication.getPrincipal();
                returnVal = tokenManager.createNewToken(userContext);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (AuthenticationException e) {
            LOGGER.error("Error authenticating for user " + login, e);
        }

        return returnVal;
    }

    @Override
    public boolean checkToken(String token) {

        UserDetails userDetails = tokenManager.getUserDetails(token);
        if (userDetails == null) {
            return false;
        }

        UsernamePasswordAuthenticationToken securityToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(securityToken);

        return true;
    }

    @Override
    public void logout(String token) {
        UserDetails logoutUser = tokenManager.removeToken(token);
        System.out.println(" *** AuthenticationServiceImpl.logout: " + logoutUser);
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return null;
    }
}
