package org.gobiiproject.gobiiclient.core.gobii;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiclient.core.common.HttpCore;
import org.gobiiproject.gobiiclient.core.common.HttpMethodResult;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;

import org.gobiiproject.gobiimodel.types.RestMethodTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Phil on 5/13/2016.
 */
public class GobiiEnvelopeRestResource<T> {

    Logger LOGGER = LoggerFactory.getLogger(GobiiEnvelopeRestResource.class);

    private RestUri restUri;
    private ObjectMapper objectMapper = new ObjectMapper();
    private GobiiPayloadResponse<T> gobiiPayloadResponse = null;

    public GobiiEnvelopeRestResource(RestUri restUri) {
        this.restUri = restUri;
        this.gobiiPayloadResponse = new GobiiPayloadResponse<>(this.restUri);
    }

    public void setParamValue(String paramName, String value) throws Exception {
        restUri.setParamValue(paramName, value);
    }


    private GobiiClientContext getClientContext() throws Exception {

        return GobiiClientContext.getInstance(null, false);
    }

    private HttpCore getHttp() throws Exception {

        return GobiiClientContext.getInstance(null, false).getHttp();
    }


    private String makeHttpBody(PayloadEnvelope<T> payloadEnvelope) throws Exception {

        String returnVal = null;

        if (payloadEnvelope.getPayload().getData().size() > 0) {
            returnVal = this.objectMapper.writeValueAsString(payloadEnvelope);
        }

        return returnVal;
    }


    public PayloadEnvelope<T> get(Class<T> dtoType) throws Exception {

        PayloadEnvelope<T> returnVal;

        HttpMethodResult httpMethodResult =
                getHttp()
                        .get(this.restUri);

        returnVal = this.gobiiPayloadResponse.getPayloadFromResponse(dtoType,
                RestMethodTypes.GET,
                HttpStatus.SC_OK,
                httpMethodResult);

        return returnVal;
    }


    public PayloadEnvelope<T> post(Class<T> dtoType,
                                   PayloadEnvelope<T> requestPayload) throws Exception {

        PayloadEnvelope<T> returnVal;

        String postBody = this.makeHttpBody(requestPayload);
        HttpMethodResult httpMethodResult =
                getHttp()
                        .post(this.restUri,
                                postBody);

        returnVal = this.gobiiPayloadResponse.getPayloadFromResponse(dtoType,
                RestMethodTypes.POST,
                HttpStatus.SC_CREATED,
                httpMethodResult);

        return returnVal;

    }

    public PayloadEnvelope<T> put(Class<T> dtoType,
                                  PayloadEnvelope<T> requestPayload) throws Exception {

        PayloadEnvelope<T> returnVal;

        String putBody = this.makeHttpBody(requestPayload);
        HttpMethodResult httpMethodResult =
                getHttp()
                        .put(this.restUri,
                                putBody,
                                this.getClientContext().getUserToken());

        returnVal = this.gobiiPayloadResponse.getPayloadFromResponse(dtoType,
                RestMethodTypes.PUT,
                HttpStatus.SC_OK,
                httpMethodResult);

        return returnVal;

    }


    public PayloadEnvelope<T> patch(Class<T> dtoType,
                                    PayloadEnvelope<T> requestPayload) throws Exception {

        PayloadEnvelope<T> returnVal = new PayloadEnvelope<>();

        returnVal.getHeader().getStatus().addStatusMessage(GobiiStatusLevel.ERROR, "Method not implemented");


        return returnVal;

    }

    public PayloadEnvelope<T> delete(Class<T> dtoType) throws Exception {

        PayloadEnvelope<T> returnVal;

        HttpMethodResult httpMethodResult =
                getHttp()
                        .delete(this.restUri);

        returnVal = this.gobiiPayloadResponse.getPayloadFromResponse(dtoType,
                RestMethodTypes.DELETE,
                HttpStatus.SC_OK,
                httpMethodResult);

        return returnVal;
    }

    public PayloadEnvelope<T> options(Class<T> dtoType,
                                      PayloadEnvelope<T> requestPayload) throws Exception {

        PayloadEnvelope<T> returnVal = new PayloadEnvelope<>();

        returnVal.getHeader().getStatus().addStatusMessage(GobiiStatusLevel.ERROR, "Method not implemented");


        return returnVal;

    }

}