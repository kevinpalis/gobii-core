/**
 * ProjectController.java
 * Gobii API endpoint controllers for projects
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-03-06
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gobiiproject.gobiiapimodel.payload.HeaderAuth;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.system.AuthDTO;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiiweb.automation.PayloadWriter;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Scope(value = "request")
@RestController
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@Api()
@CrossOrigin
@Slf4j
public class GOBIIControllerV3  {
    


    /**
     * Authentication Endpoint
     * Mimicking same logic used in v1
     * @param request - Request from the client
     * @param response - Response with Headers values filled in TokenFilter
     * @return
     */
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<HeaderAuth> authenticate(HttpServletRequest request,
                                       HttpServletResponse response) {

        try {

            HeaderAuth dtoHeaderAuth = new HeaderAuth();

            PayloadWriter<AuthDTO> payloadWriter = new PayloadWriter<>(
                    request, response, AuthDTO.class);

            payloadWriter.setAuthHeader(dtoHeaderAuth, response);

            return ResponseEntity.ok(dtoHeaderAuth);

        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "Entity does not exist"
            );
        }


    }

}