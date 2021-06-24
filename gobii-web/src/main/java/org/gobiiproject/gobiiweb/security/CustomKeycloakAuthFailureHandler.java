package org.gobiiproject.gobiiweb.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gobiiproject.gobiiweb.automation.ResponseUtils;
import org.keycloak.adapters.springsecurity.authentication.KeycloakCookieBasedRedirect;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class CustomKeycloakAuthFailureHandler implements AuthenticationFailureHandler {
    

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        if (!response.isCommitted()) {
            if (KeycloakCookieBasedRedirect.getRedirectUrlFromCookie(request) != null) {
                response.addCookie(KeycloakCookieBasedRedirect.createCookieFromRedirectUrl(null));
            }
            ResponseUtils.sendUnauthorizedResponse(response);
        } else {
            if (200 <= response.getStatus() && response.getStatus() < 300) {
                throw new RuntimeException("Success response was committed while authentication failed!", exception);
            }
        }

    }
    
}