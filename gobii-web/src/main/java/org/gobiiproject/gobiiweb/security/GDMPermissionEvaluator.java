package org.gobiiproject.gobiiweb.security;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GDMPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();
        return this.hasPermission(authentication, null, targetType, permission);
    }

    @Override
    @SuppressWarnings("all")
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        
        //check if the auth principal is a keycloak principal
        if (Optional.ofNullable(authentication)
                    .map(a -> a.getPrincipal())
                    .orElse(null) instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
            AccessToken token = kp.getKeycloakSecurityContext().getToken();
            Map<String, Object> otherClaims = token.getOtherClaims();
            if (isAdmin(otherClaims)) return true;

            //the CropUserFilter would have checked the user access to the crop so just check the group
        
            String currentCropType = null;
            try {
                currentCropType = CropRequestAnalyzer.getGobiiCropType().toLowerCase();
            } catch (Exception e) {
                log.error("Cannot find Gobii crop type");
                return false;
            }

            //Get user groups
            List<String> groups = (List<String>) otherClaims.get("groups");
            final String rootPath = String.format("/%s", currentCropType);
            groups.removeIf(element -> !element.startsWith(String.format("/%s", rootPath)));

            if (groups.contains(String.format("/%s/curator", currentCropType))) {
                return true; //since curator is like an admin for this crop
            }

            if (groups.contains(String.format("/%s/pi", currentCropType))) {
                return false; 
            }

            //no need to check for /USER since all endpoints withouth @PreAuthorize are by default
            //accessible by Users
            System.out.println("Rejected at evaluator");
            return false; //default permission
        }
        return true;
    }

    private boolean isAdmin(Map<String, Object> otherClaims) {
        return  Optional.ofNullable(otherClaims)
                .map(m ->
                    Optional.ofNullable(m.get("roles"))
                        .map(roles -> (roles instanceof List<?>) &&
                                      ((List<?>) roles).contains("ADMIN")
                            )
                        .orElse(false)
                )
                .orElse(false);
    }
}
