package org.gobiiproject.gobiiweb.controllers.brapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;

import org.gobiiproject.gobiidomain.services.gdmv3.KeycloakService;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiibrapi.calls.login.BrapiRequestLogin;
import org.gobiiproject.gobiibrapi.calls.login.BrapiResponseLogin;
import org.gobiiproject.gobiibrapi.core.common.BrapiPagination;
import org.gobiiproject.gobiibrapi.core.common.BrapiRequestReader;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.security.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Scope(value = "request")
@Controller
@RequestMapping(value = {
        GobiiControllerType.SERVICE_PATH_BRAPI,
        GobiiControllerType.SERVICE_PATH_BRAPI_V2
})
@CrossOrigin
public class BrAPIAuthController {

    private ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Autowired
    private KeycloakService keycloakService;

    @RequestMapping(value = "/token",
            method = RequestMethod.POST,
            produces = "application/json")
    @ApiOperation(
            value = "Authentication",
            notes = "Returns a API Key if authentication is successful",
            tags = {"Authentication"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Authentication"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String postLogin(@RequestBody String loginRequestBody,
                            HttpServletResponse response) throws Exception {

        String returnVal;

        BrapiResponseLogin brapiResponseLogin = new BrapiResponseLogin();

        try {

            BrapiRequestReader<BrapiRequestLogin> brapiRequestReader = new BrapiRequestReader<>(
                    BrapiRequestLogin.class);

            BrapiRequestLogin brapiRequestLogin = brapiRequestReader.makeRequestObj(loginRequestBody);

            //BrapiResponseMapLogin brapiResponseMapLogin = new BrapiResponseMapLogin();

            //brapiResponseLogin = brapiResponseMapLogin.getLoginInfo(brapiRequestLogin, response);

            TokenInfo tokenInfo = keycloakService.getToken(
                    brapiRequestLogin.getUserName(),
                    brapiRequestLogin.getPassword()
            );
            

            brapiResponseLogin.setAccessToken(tokenInfo.getAccessToken());
            brapiResponseLogin.setUserDisplayName(brapiRequestLogin.getUserName());
            brapiResponseLogin.setExpiresIn("" + tokenInfo.getExpiry());

            brapiResponseLogin.getBrapiMetaData().setPagination(new BrapiPagination(
                    1,
                    1,
                    1,
                    0
            ));

        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseLogin.getBrapiMetaData().addStatusMessage("exception", message);

        } catch (Exception e) {
        
            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseLogin.getBrapiMetaData().addStatusMessage("exception", message);
        }

        returnVal = objectMapper.writeValueAsString(brapiResponseLogin);

        return returnVal;

    }


}
