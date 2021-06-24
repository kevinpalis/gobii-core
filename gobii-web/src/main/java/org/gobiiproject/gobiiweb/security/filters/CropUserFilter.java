package org.gobiiproject.gobiiweb.security.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.gobiiproject.gobiidomain.services.gdmv3.ContactService;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiiapimodel.types.GobiiHttpHeaderNames;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiimodel.utils.URLUtils;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.automation.ResponseUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import lombok.extern.slf4j.Slf4j;

/**
 * This filter determines if the user is an ADMIN OR has permission to access the crop endpoint
 * The user must have /{cropType} group at least OR has and ADMIN role. If the user is permitted 
 * to access the endpoint, the user will be added to the Contacts table if the user record does not
 * yet exist in the GDM database.
 */
@Slf4j
public class CropUserFilter extends GenericFilterBean {

    @Autowired
    ContactService contactService;

    @Override
    @SuppressWarnings("all")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String servicePath = httpServletRequest.getRequestURI().substring(
            httpServletRequest.getContextPath().length());

        //TODO:  move these two conditions to filter config
        // Skip crop user filter for /crops endpoint
        if(URLUtils.stripStartAndEndPathSeparator(servicePath)
            .equals(URLUtils.stripStartAndEndPathSeparator(
                GobiiControllerType.CROPS.getControllerPath()))) {

            chain.doFilter(request, response); //continue on
            return;

        }

         // Skip crop user filter for /crops endpoint
        if(URLUtils.stripStartAndEndPathSeparator(servicePath)
            .equals(URLUtils.stripStartAndEndPathSeparator(
                "/config/extractor"))) {

            chain.doFilter(request, response); //continue on
            return;

        }
        //skip crop user filter for /contacts/admin
        if(servicePath.endsWith("/contacts/admin")) {
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest req = (HttpServletRequest) request;
        if (!this.shouldFilter(req.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        if (Optional.ofNullable(authentication)
                    .map(a -> a.getPrincipal())
                    .orElse(null) instanceof KeycloakPrincipal
            ) {
            
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            //Add the response headers
            String currentCropType = null;
            try {
                currentCropType = CropRequestAnalyzer.getGobiiCropType(httpServletRequest).toLowerCase();
                httpResponse.setHeader(GobiiHttpHeaderNames.HEADER_NAME_GOBII_CROP, currentCropType);
            } catch (Exception e) {
                throw new ServletException(e);
            }
            KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
            AccessToken token = kp.getKeycloakSecurityContext().getToken();
            //Get the major roles
            //and append
            Map<String, Object> otherClaims = token.getOtherClaims(); 
            //TODO:  including these in the response header seems to be a security risk.
            httpResponse.setHeader(GobiiHttpHeaderNames.HEADER_NAME_TOKEN, token.getAccessTokenHash());
            httpResponse.setHeader(GobiiHttpHeaderNames.HEADER_NAME_USERNAME, token.getPreferredUsername());
            //bypass if admin
            List<String> roles = (List<String>) Optional.ofNullable(otherClaims.get("roles")).orElse(new ArrayList<>());
            if (roles.contains("ADMIN"))  {
                try {
                    this.addToContacts(token);
                } catch (Exception e) {
                    //e.printStackTrace();
                    log.error("Could not add admin info to contacts table");
                }
                chain.doFilter(request, response); //continue on
                return;
            }

            List<String> userGroups = (List<String>) Optional.ofNullable(otherClaims.get("groups")).orElse(new ArrayList<>());
            Iterator<String> iter = userGroups.iterator();
            while (iter.hasNext()) {
                if (iter.next()
                        .toString()
                        .startsWith(
                            String.format("/%s", currentCropType)
                        )
                    ) {
                    //Check if the user is in the db, add if not found
                    try {
                        this.addToContacts(token);
                    } catch (GobiiException e) {
                        ErrorPayload payload = new ErrorPayload();
                        payload.setError(e.getMessage());
                        ObjectMapper mapper = new ObjectMapper();
                        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                        httpResponse.getWriter().write(mapper.writeValueAsString(payload));
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chain.doFilter(request, response);
                    return;
                }
               
            }
            
            ResponseUtils.sendUnauthorizedResponse(response);
            return;
        } 

        chain.doFilter(request, response);

    }


    private void addToContacts(AccessToken token) throws Exception, GobiiException {
        String organization = Optional
                              .ofNullable(token.getOtherClaims().get("organization"))
                              .map(o -> o.toString())
                              .orElse(null);
        // check null check on email
        if (LineUtils.isNullOrEmpty(token.getEmail())) {
            throw new GobiiException(GobiiStatusLevel.ERROR,
                                     GobiiValidationStatusType.BAD_REQUEST,
                                     "User does not have email id. Please contact admin to update their email.");  
        }                            
        contactService.addContact(
            token.getPreferredUsername(),
            token.getGivenName(),
            token.getFamilyName(),
            token.getEmail(),
            organization,
            null
        );

    }

    private boolean shouldFilter(String path) {
        if (path.contains("/gobii/v3")) return true;
        if (path.contains("/gobii/v1")) return true;
        return false;
    }
    
    
}