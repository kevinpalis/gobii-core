// ************************************************************************
// (c) 2016 GOBii Projects
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiiweb.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gobiiproject.gobiidomain.security.TokenInfo;
import org.gobiiproject.gobiidomain.services.AuthenticationService;
import org.gobiiproject.gobiidomain.services.ContactService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiiapimodel.types.GobiiHttpHeaderNames;
import org.gobiiproject.gobiibrapi.calls.login.BrapiRequestLogin;
import org.gobiiproject.gobiibrapi.core.common.BrapiRequestReader;
import org.gobiiproject.gobiibrapi.types.BRAPIHttpHeaderNames;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.automation.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Takes care of HTTP request/response pre-processing for login/logout and token check.
 * Login can be performed on any URL, logout only on specified {@link #logoutLink}.
 * All the interaction with Spring Security should be performed via {@link AuthenticationService}.
 * <p>
 * {@link SecurityContextHolder} is used here only for debug outputs. While this class
 * is configured to be used by Spring Security (configured filter on FORM_LOGIN_FILTER position),
 * but it doesn't really depend on it at all.
 */
@Deprecated //due to Keycloak integration
public final class TokenAuthenticationFilter extends GenericFilterBean {

    Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    private final String logoutLink;
    private final AuthenticationService authenticationService;
    private ContactService contactService;


    public TokenAuthenticationFilter(AuthenticationService authenticationService,
                                     String logoutLink,
                                     ContactService contactService) {

        this.authenticationService = authenticationService;
        this.logoutLink = logoutLink;
        this.contactService = contactService;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //HttpServletRequest httpRequest = null;
        HttpServletResponse httpResponse = null;

        try {
            AuthenticationRequestWrapper authenticationRequestWrapper =
                    new AuthenticationRequestWrapper((HttpServletRequest) request);

            httpResponse = (HttpServletResponse) response;


            String gobiiCropType = CropRequestAnalyzer.getGobiiCropType(authenticationRequestWrapper);
            if (gobiiCropType != null) {


                String url = authenticationRequestWrapper.getRequestURL().toString();

                // BRAPI is using the "Authorization" header for its token value, which we had been using
                // to indicate whether or not to do basic authentication; so we need to rework how we
                // work with this variable here
                String rawBearerTokenHeader = authenticationRequestWrapper.getHeader(BRAPIHttpHeaderNames.HEADER_NAME_TOKEN);

                String tokenHeaderVal = null;
                if (LineUtils.isNullOrEmpty(rawBearerTokenHeader)) {
                    tokenHeaderVal = authenticationRequestWrapper.getHeader(GobiiHttpHeaderNames.HEADER_NAME_TOKEN);
                } else {

                    // useful code to probe header values when you need to
//                    Enumeration<String> headerNames = authenticationRequestWrapper.getHeaderNames();
//                    while(headerNames.hasMoreElements()) {
//                        String headerName = headerNames.nextElement();
//                        Enumeration<String> headers = authenticationRequestWrapper.getHeaders(headerName);
//                        while(headers.hasMoreElements()) {
//                            String headerValue = headers.nextElement();
//                            String temp = "foo";
//                        }
//                    }

                        if (rawBearerTokenHeader.contains("Bearer")) {
                            tokenHeaderVal = rawBearerTokenHeader.replace("Bearer", "").trim();
                        } else {
                            // this way the logic for whether we're doing Basic authentication below works out
                            rawBearerTokenHeader = null;
                        }

                } // if-else we are dealing with a Bearer token or a gobii token

                boolean hasValidToken = authenticationService.checkToken(tokenHeaderVal);

                if (hasValidToken) {

                    //header data
                    this.addHeadersToValidRequest(httpResponse, null, gobiiCropType, tokenHeaderVal);
                    chain.doFilter(authenticationRequestWrapper, response);
                } else {

                    // there was not a valid token, so now we need to authenticate
                    TokenInfo tokenInfo = null;
                    String userName = null;
                    String password = null;


                    boolean isBrapiRequest = url.toLowerCase().contains(GobiiControllerType.SERVICE_PATH_BRAPI)
                            || url.toLowerCase().contains(GobiiControllerType.SERVICE_PATH_BRAPI_V2);
                    if (!isBrapiRequest) {
                        userName = authenticationRequestWrapper.getHeader(GobiiHttpHeaderNames.HEADER_NAME_USERNAME);
                        password = authenticationRequestWrapper.getHeader(GobiiHttpHeaderNames.HEADER_NAME_PASSWORD);
                    } else { // we're dealing with BRAPI, so we get the username/password from the body

                        String method = authenticationRequestWrapper.getMethod();
                        if ("POST".equalsIgnoreCase(method) && url.toLowerCase().contains(RestResourceId.BRAPI_LOGIN.getResourcePath())) {

                            String loginRequestBody = authenticationRequestWrapper.getRequestBodyAsString();
                            if (loginRequestBody != null) {
                                BrapiRequestReader<BrapiRequestLogin> brapiRequestReader = new BrapiRequestReader<>(BrapiRequestLogin.class);
                                BrapiRequestLogin brapiRequestLogin = brapiRequestReader.makeRequestObj(loginRequestBody.toString());
                                userName = brapiRequestLogin.getUserName();
                                password = brapiRequestLogin.getPassword();
                            }
                        }
                    } // if-else this is not a BRAPI request


                    // we assume that the DataSource selector will have done the right thing with the response
                    // we are just echoing back to the client (the web client needs this)

                    if (null == rawBearerTokenHeader) {

                        // we're doing HTTP post authentication
                        tokenInfo = authenticationService.authenticate(userName, password);

                    } else {
                        tokenInfo = checkBasicAuthorization(rawBearerTokenHeader, httpResponse);

                    } // if else we're going basic authentication

                    if (null != tokenInfo) {

                        if (this.contactService.getContactByUserName(userName).getContactId() > 0) {

                            this.addHeadersToValidRequest(
                                    httpResponse, userName,
                                    gobiiCropType, tokenInfo.getToken()
                            );
                            chain.doFilter(authenticationRequestWrapper, response);

                        } else {

                            String message = "Missing contact info for user "
                                    + userName
                                    + " in crop database "
                                    + gobiiCropType
                                    + "; a contact record must have username = "
                                    + userName;

                            ErrorPayload errorPayload = new ErrorPayload();
                            errorPayload.setError(message);
                            ObjectMapper objMapper = new ObjectMapper();
                            String errorBody = objMapper.writeValueAsString(errorPayload);

                            ControllerUtils.writeRawResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN, errorBody);

                            LOGGER.error(message);
                        }

                    } else {
                        ErrorPayload errorPayload = new ErrorPayload();
                        errorPayload.setError("The request has not been applied because it lacks " +
                                "valid authentication credentials for the target resource.");
                        ObjectMapper objMapper = new ObjectMapper();
                        String errorBody = objMapper.writeValueAsString(errorPayload);
                        ControllerUtils.writeRawResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, errorBody);
                    } // if-else the user authenticated
                }

            } else {

                ErrorPayload errorPayload = new ErrorPayload();
                errorPayload.setError("Unable to proceed with authentication: " +
                        "no crop type could be derived from the request url");
                ObjectMapper objMapper = new ObjectMapper();
                String errorBody = objMapper.writeValueAsString(errorPayload);
                ControllerUtils.writeRawResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, errorBody);
                LOGGER.error("Unable to proceed with authentication: " +
                        "no crop type could be derived from the request url");

            } // if-else crop type could not be found


        } catch (Exception e) {

            LOGGER.error("Error in authentication filter", e);

            if (httpResponse != null) {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
            }
        }

    } // doFilter()


    private void addHeadersToValidRequest(HttpServletResponse httpResponse,
                                          String userName,
                                          String gobiiCropType,
                                          String token) throws Exception {


        httpResponse.setHeader(GobiiHttpHeaderNames.HEADER_NAME_TOKEN, token);
        httpResponse.setHeader(GobiiHttpHeaderNames.HEADER_NAME_GOBII_CROP, gobiiCropType);
        httpResponse.setHeader(GobiiHttpHeaderNames.HEADER_NAME_USERNAME, userName);


    }

    private TokenInfo checkBasicAuthorization(String authorization, HttpServletResponse httpResponse) throws IOException {

        TokenInfo returnVal = null;

        StringTokenizer tokenizer = new StringTokenizer(authorization);
        if ((tokenizer.countTokens() >= 2)
                && tokenizer.nextToken().equalsIgnoreCase("Basic")) {

            String base64 = tokenizer.nextToken();
            String loginPassword = new String(Base64.decode(base64.getBytes(StandardCharsets.UTF_8)));

            System.out.println("loginPassword = " + loginPassword);
            tokenizer = new StringTokenizer(loginPassword, ":");
            System.out.println("tokenizer = " + tokenizer);
            String userName = tokenizer.nextToken();
            String password = tokenizer.nextToken();
            returnVal = authenticationService.authenticate(userName, password);
        }

        return returnVal;
    }

    @SuppressWarnings("unused")
    private void checkLogout(HttpServletRequest httpRequest) {
        if (currentLink(httpRequest).equals(logoutLink)) {
            String token = httpRequest.getHeader(GobiiHttpHeaderNames.HEADER_NAME_TOKEN);
            // we go here only authenticated, token must not be null
            authenticationService.logout(token);
        }
    }

    // or use Springs util instead: new UrlPathHelper().getPathWithinApplication(httpRequest)
    // shame on Servlet API for not providing this without any hassle :-(
    private String currentLink(HttpServletRequest httpRequest) {
        if (httpRequest.getPathInfo() == null) {
            return httpRequest.getServletPath();
        }
        return httpRequest.getServletPath() + httpRequest.getPathInfo();
    }

}
