package org.gobiiproject.gobiidomain.services.gdmv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.gdmv3.ContactDTO;
import org.gobiiproject.gobiimodel.security.TokenInfo;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public interface KeycloakService {
    ContactDTO getUser(String uuid) throws Exception;
    ContactDTO getUserByUserName(String userName) throws GobiiDomainException;

    List<ContactDTO> getKeycloakUsers(String cropType, String role, Integer page, Integer pageSize) throws Exception;
    List<ContactDTO> getKeycloakRealmAdmin() throws Exception;

	TokenInfo getToken(String username, String password) throws Exception;


    /**
     * @return AccessToken from current securitycontext holder
     */
    static AccessToken getAccessToken() {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication.getPrincipal() instanceof KeycloakPrincipal) {
                KeycloakPrincipal<org.keycloak.KeycloakSecurityContext> kp =
                    (KeycloakPrincipal<org.keycloak.KeycloakSecurityContext>)
                        authentication.getPrincipal();
                AccessToken token = kp.getKeycloakSecurityContext().getToken();
                return token;
            }
            else {
                throw new GobiiDomainException("Unable to fetch authorization information");
            }


        }
        catch (Exception e) {
            Logger LOGGER = LoggerFactory.getLogger(KeycloakService.class);
            LOGGER.error("Unable to fetch authorization information.", e);
            throw new GobiiDomainException(e);

        }
    }

    /**
     * @return UserGroups in keycloak Access Token
     */
    static List<String> getUserGroups() {
        try {
            Map<String, Object> otherClaims = getAccessToken().getOtherClaims();

            List<String> userGroups = (List<String>) Optional.ofNullable(otherClaims.get("groups"))
                .orElse(new ArrayList<>());
            return userGroups;
        }
        catch (Exception e) {
            Logger LOGGER = LoggerFactory.getLogger(KeycloakService.class);
            LOGGER.error("Unable to fetch authorization information.", e);
            throw new GobiiDomainException(e);
        }
    }

}