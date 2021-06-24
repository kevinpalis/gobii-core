package org.gobiiproject.gobiiclient.core.brapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.restresources.gobii.GobiiUriFactory;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiibrapi.calls.login.BrapiRequestLogin;
import org.gobiiproject.gobiibrapi.calls.login.BrapiResponseLogin;
import org.gobiiproject.gobiibrapi.core.common.BrapiRequestReader;
import org.gobiiproject.gobiiclient.core.common.HttpCore;
import org.gobiiproject.gobiiclient.core.common.HttpMethodResult;
import org.gobiiproject.gobiiclient.core.gobii.GobiiTestConfiguration;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.config.TestExecConfig;
import org.gobiiproject.gobiimodel.types.ServerType;


/**
 * Created by Phil on 5/13/2016.
 */
public class BrapiClientContextAuth {


    public static HttpCore authenticate() throws Exception {

        HttpCore returnVal = null;

        TestExecConfig testExecConfig = (new GobiiTestConfiguration()).getConfigSettings().getTestExecConfig();
        String testCrop = testExecConfig.getTestCrop();
        GobiiCropConfig gobiiCropConfig = (new GobiiTestConfiguration()).getConfigSettings().getCropConfig(testCrop);


        returnVal = new HttpCore(
            gobiiCropConfig.getServer(ServerType.GOBII_WEB).getHost(),
            gobiiCropConfig.getServer(ServerType.GOBII_WEB).getPort());

        // this method assumes we've already initialized the context with the server URL
        String testUserName = testExecConfig.getLdapUserForUnitTest();
        String testPassword = testExecConfig.getLdapPasswordForUnitTest();


        BrapiRequestLogin brapiRequestLogin = new BrapiRequestLogin();
        brapiRequestLogin.setUserName(testUserName);
        brapiRequestLogin.setPassword(testPassword);

        GobiiUriFactory gobiiUriFactory = new GobiiUriFactory(
            gobiiCropConfig.getServer(ServerType.GOBII_WEB).getContextPath(),
            GobiiControllerType.BRAPI,
            gobiiCropConfig.getGobiiCropType());

        RestUri restUriToken = gobiiUriFactory
                .resourceColl(RestResourceId.BRAPI_LOGIN);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(brapiRequestLogin);
        HttpMethodResult httpMethodResult = returnVal.post(restUriToken, requestBody);

        if (httpMethodResult.getResponseCode() == HttpServletResponse.SC_OK) {

            BrapiRequestReader<BrapiResponseLogin> brapiRequestReader = new BrapiRequestReader<>(BrapiResponseLogin.class);
            BrapiResponseLogin brapiResponseLogin = brapiRequestReader.makeRequestObj(httpMethodResult.getJsonPayload().toString());
            returnVal.setTokenType(HttpCore.TokenType.BEARER);
            returnVal.setToken(brapiResponseLogin.getAccessToken());

        } else {

            String message = "BRAPI authentication error to "
                    + restUriToken.toString()
                    + ": " + Integer.toString(httpMethodResult.getResponseCode())
                    + " " + httpMethodResult.getReasonPhrase()
                    + ": " + httpMethodResult.getMessage();

            throw new Exception(message);
        }



        return returnVal;
    }

}
