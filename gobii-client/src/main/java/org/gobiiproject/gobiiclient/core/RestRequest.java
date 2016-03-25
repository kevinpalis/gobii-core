// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-25
// ************************************************************************
package org.gobiiproject.gobiiclient.core;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.net.URI;


public class RestRequest<T> {

    private final Class<T> paramType;

    @SuppressWarnings("unchecked")
    public RestRequest(Class<T> paramType) {
        this.paramType = paramType;
    } // ctor

    Logger LOGGER = LoggerFactory.getLogger(RestRequest.class);


    private static final String HEADER_TOKEN = "X-Auth-Token";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_PASSWORD = "X-Password";


    final private static int HTTP_STATUS_CODE_OK = 200;
    final private static int HTTP_STATUS_CODE_UNAUTHORIZED = 401;  //not authenticated
    final private static int HTTP_STATUS_CODE_FORBIDDEN = 403; //not authorized

    private URIBuilder getBaseBuilder() throws Exception {
        return (new URIBuilder().setScheme("http")
                .setHost("localhost")
                .setPort(8181));
    }

    private URI makeUri(String url) throws Exception {

        URI returnVal = getBaseBuilder()
                .setPath(url)
                .build();

        return (returnVal);

    }//byContentTypeUri()

    private URI getMarkerUri() throws Exception {
        return (makeUri(Urls.URL_MARKERS));
    } // getMarkerUri()

    private URI resourceUri() throws Exception {

        URI returnVal = getBaseBuilder()
                .setPath("/resource")
                .build();

        return (returnVal);
    }//byContentTypeUri()

    private URI logoutURI() throws Exception {

        URI returnVal = getBaseBuilder()
                .setPath("/logout") //configured in security configuration xml
                .build();

        return (returnVal);
    }//byContentTypeUri()

    private HttpResponse submitUriRequest(HttpUriRequest httpUriRequest, String userName, String password, String token) throws Exception {
        httpUriRequest.addHeader("Content-Type", "application/json");
        httpUriRequest.addHeader("Accept", "application/json");

        if ((null != token) && (false == token.isEmpty())) {
            httpUriRequest.addHeader(HEADER_TOKEN, token);
        } else {
            httpUriRequest.addHeader(HEADER_USERNAME, userName);
            httpUriRequest.addHeader(HEADER_PASSWORD, password);
        }

        return (HttpClientBuilder.create().build().execute(httpUriRequest));

    }// submitUriRequest()


    private void logInfo(String logMessage) {
        LOGGER.info(logMessage);
    } // logInfo() 

    private void logHeaders(Header[] headers) {

        for (Header currentHeader : headers) {
            logInfo(currentHeader.getName() + ": " + currentHeader.getValue());
        }

    }//logHeaders()

    private Header getHeader(Header[] headers, String headerName) {
        Header returnVal = null;

        boolean foundHeader = false;
        for (int idx = 0; idx < headers.length && foundHeader == false; idx++) {
            Header currentHeader = headers[idx];

            if (headerName.equals(currentHeader.getName())) {
                returnVal = currentHeader;
                foundHeader = true;
            }
        }//iterate headers

        return (returnVal);

    }//getHeader()

    private void logRequestHeaders(HttpUriRequest httpUriRequest, HttpResponse httpResponse, String testName) throws Exception {

        StringBuilder stringBuilder = new StringBuilder();


        stringBuilder.append("============================================ BEGIN TEST " + testName + "==================================");
        stringBuilder.append("");
        stringBuilder.append("");
        stringBuilder.append("");
        stringBuilder.append("****************");
        stringBuilder.append("****************");
        stringBuilder.append("****************** Request URL");
        stringBuilder.append(httpUriRequest.getURI().toString());


        stringBuilder.append("");
        stringBuilder.append("");
        stringBuilder.append("");
        stringBuilder.append("****************");
        stringBuilder.append("****************");
        stringBuilder.append("****************** Request Headers");
        logHeaders(httpUriRequest.getAllHeaders());

        stringBuilder.append("");
        stringBuilder.append("");
        stringBuilder.append("");
        stringBuilder.append("****************");
        stringBuilder.append("****************");
        stringBuilder.append("****************** Response  Headers");
        logHeaders(httpResponse.getAllHeaders());

        stringBuilder.append("");
        stringBuilder.append("");
        stringBuilder.append("");
        stringBuilder.append("****************");
        stringBuilder.append("****************");
        stringBuilder.append("****************** Response  Status");
        stringBuilder.append(httpResponse.getStatusLine());

        stringBuilder.append("");
        stringBuilder.append("");
        stringBuilder.append("");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader((httpResponse.getEntity().getContent())));

        String output;
        stringBuilder.append("****************");
        stringBuilder.append("****************");
        stringBuilder.append("*****************Response Body content");
        while ((output = bufferedReader.readLine()) != null) {
            stringBuilder.append(output);
        }

        stringBuilder.append("============================================ END  TEST " + testName + "==================================");


    }//logRequestHeaders()

    private HttpResponse authenticateWithUser(String userName, String password) throws Exception {

        HttpResponse returnVal = null;

        URI uri = getMarkerUri();
        HttpPost postRequest = new HttpPost(uri);
        returnVal = submitUriRequest(postRequest, userName, password, null);

        logRequestHeaders(postRequest, returnVal, " Authenticate with user " + userName);

        return (returnVal);

    }//authenticateWithUser()

    public String getTokenForUser(String userName, String password) throws Exception {

        String returnVal = null;

        HttpResponse response = authenticateWithUser(userName, password);
        Header tokenHeader = getHeader(response.getAllHeaders(), HEADER_TOKEN);
        returnVal = tokenHeader.getValue();

        if (null == returnVal) {
            LOGGER.error("Unable to get authentication token for user " + userName);
        }

        return returnVal;

    } // getTokenForUser()


    public T getTypedHtppResponse(String url,
                                  JsonObject requestJson,
                                  String token) throws Exception {

        T returnVal = null;

        JsonObject responseJson = getResponseBody(url, requestJson, token);

        ObjectMapper objectMapper = new ObjectMapper();
        returnVal = objectMapper.readValue(responseJson.toString(), paramType);

        return returnVal;

    } // getTypedHtppResponse()


    public JsonObject getResponseBody(String url,
                                      JsonObject jsonObject,
                                      String token) throws Exception {

        JsonObject returnVal = null;

        HttpResponse httpResponse = null;

        URI uri = makeUri(url);

        HttpPost postRequest = new HttpPost(uri);
        String jsonString = jsonObject.toString();
        StringEntity input = new StringEntity(jsonString);
        postRequest.setEntity(input);

        httpResponse = submitUriRequest(postRequest, "", "", token);

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader((httpResponse.getEntity().getContent())));

        StringBuilder stringBuilder = new StringBuilder();
        String currentLine = null;
        while ((currentLine = bufferedReader.readLine()) != null) {
            stringBuilder.append(currentLine);
        }


        JsonParser parser = new JsonParser();
        returnVal = parser.parse(stringBuilder.toString()).getAsJsonObject();

        return returnVal;

    }//accessResource_test


}// ArgumentDAOTest
