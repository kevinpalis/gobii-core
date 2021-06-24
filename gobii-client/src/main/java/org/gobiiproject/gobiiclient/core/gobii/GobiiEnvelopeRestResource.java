package org.gobiiproject.gobiiclient.core.gobii;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import org.apache.http.HttpStatus;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiclient.core.common.HttpCore;
import org.gobiiproject.gobiiclient.core.common.HttpMethodResult;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.RestMethodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class adds the GOBII-specific type handling on top of the generic
 * HTTP functionality of HttpCore. It wraps the generic HTTP method methods
 * (get, post, put, patch, delete) provided by HTTP core. It serializes and
 * deserializes GOBII's POJO DTO payloads. Some of the functionality here is
 * general enough that it could be used for non-GOBII payloads that are,
 * like the GOBII DTOs, expressed as POJO. However, the structure of the GOBII
 * response is very specific. If in the future this client library is required to support
 * non-GOBII services that merit using POJOs similar to DTOs, the generic type-based
 * functionality of this class could be separated out into a common class that
 * would be consumed by this class and a similar new class for the non-GOBII
 * payloads.
 */
public class GobiiEnvelopeRestResource<T_REQUEST_BODY_TYPE, T_RESPONSE_BODY_TYPE> {

    Logger LOGGER = LoggerFactory.getLogger(GobiiEnvelopeRestResource.class);

    private RestUri restUri;
    private ObjectMapper objectMapper = new ObjectMapper();
    private GobiiPayloadResponse<T_RESPONSE_BODY_TYPE> gobiiPayloadResponse = null;

    public GobiiEnvelopeRestResource(RestUri restUri) {
        this.restUri = restUri;
        this.gobiiPayloadResponse = new GobiiPayloadResponse<>(this.restUri);
    }

    public void setParamValue(String paramName, String value) throws Exception {
        restUri.setParamValue(paramName, value);
    }

    @SuppressWarnings("unused")
    private GobiiClientContext getClientContext() throws Exception {

        return GobiiClientContext.getInstance(null, false);
    }

    private HttpCore getHttp() throws Exception {

        return GobiiClientContext.getInstance(null, false).getHttp();
    }


    private String makeHttpBody(PayloadEnvelope<T_REQUEST_BODY_TYPE> payloadEnvelope) throws Exception {

        String returnVal = null;

        if (payloadEnvelope.getPayload().getData().size() > 0) {
            returnVal = this.objectMapper.writeValueAsString(payloadEnvelope);
        }

        return returnVal;
    }


    public PayloadEnvelope<T_RESPONSE_BODY_TYPE> get(Class<T_RESPONSE_BODY_TYPE> dtoResponseBodyType) throws Exception {

        PayloadEnvelope<T_RESPONSE_BODY_TYPE> returnVal;

        HttpMethodResult httpMethodResult =
                getHttp()
                        .get(this.restUri);

        returnVal = this.gobiiPayloadResponse.getPayloadFromResponse(dtoResponseBodyType,
                RestMethodType.GET,
                HttpStatus.SC_OK,
                httpMethodResult);

        return returnVal;
    }


    public PayloadEnvelope<T_RESPONSE_BODY_TYPE> post(Class<T_RESPONSE_BODY_TYPE> dtoResponseBodyType,
                                                     PayloadEnvelope<T_REQUEST_BODY_TYPE> requestPayload) throws Exception {

        PayloadEnvelope<T_RESPONSE_BODY_TYPE> returnVal;

        String postBody = this.makeHttpBody(requestPayload);
        HttpMethodResult httpMethodResult =
                getHttp()
                        .post(this.restUri,
                                postBody);

        returnVal = this.gobiiPayloadResponse.getPayloadFromResponse(dtoResponseBodyType,
                RestMethodType.POST,
                HttpStatus.SC_CREATED,
                httpMethodResult);

        return returnVal;

    }

    public HttpMethodResult upload(File file) throws Exception {

        HttpMethodResult returnVal = getHttp()
                .upload(this.restUri,
                        file);

        return returnVal;
    }

    public PayloadEnvelope<T_RESPONSE_BODY_TYPE> put(Class<T_RESPONSE_BODY_TYPE> dtoResponseBodyType,
                                                    PayloadEnvelope<T_REQUEST_BODY_TYPE> requestPayload) throws Exception {

        PayloadEnvelope<T_RESPONSE_BODY_TYPE> returnVal;

        String putBody = this.makeHttpBody(requestPayload);
        HttpMethodResult httpMethodResult =
                getHttp()
                        .put(this.restUri,
                                putBody);

        returnVal = this.gobiiPayloadResponse.getPayloadFromResponse(dtoResponseBodyType,
                RestMethodType.PUT,
                HttpStatus.SC_OK,
                httpMethodResult);

        return returnVal;

    }


    public PayloadEnvelope<T_RESPONSE_BODY_TYPE> patch(Class<T_RESPONSE_BODY_TYPE> dtoResponseBodyType,
                                                      PayloadEnvelope<T_REQUEST_BODY_TYPE> requestPayload) throws Exception {

        PayloadEnvelope<T_RESPONSE_BODY_TYPE> returnVal = new PayloadEnvelope<>();

        returnVal.getHeader().getStatus().addStatusMessage(GobiiStatusLevel.ERROR, "Method not implemented");


        return returnVal;

    }

    public PayloadEnvelope<T_RESPONSE_BODY_TYPE> delete(Class<T_RESPONSE_BODY_TYPE> dtoResponseBodyType) throws Exception {

        PayloadEnvelope<T_RESPONSE_BODY_TYPE> returnVal;

        HttpMethodResult httpMethodResult =
                getHttp()
                        .delete(this.restUri);

        returnVal = this.gobiiPayloadResponse.getPayloadFromResponse(dtoResponseBodyType,
                RestMethodType.DELETE,
                HttpStatus.SC_OK,
                httpMethodResult);

        return returnVal;
    }

    public PayloadEnvelope<T_RESPONSE_BODY_TYPE> options(Class<T_RESPONSE_BODY_TYPE> dtoResponseBodyType,
                                                        PayloadEnvelope<T_REQUEST_BODY_TYPE> requestPayload) throws Exception {

        PayloadEnvelope<T_RESPONSE_BODY_TYPE> returnVal = new PayloadEnvelope<>();

        returnVal.getHeader().getStatus().addStatusMessage(GobiiStatusLevel.ERROR, "Method not implemented");


        return returnVal;

    }

}