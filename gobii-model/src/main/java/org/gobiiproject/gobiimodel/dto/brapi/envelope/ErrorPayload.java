package org.gobiiproject.gobiimodel.dto.brapi.envelope;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Payload for error messages.
 * Null Values are ignored.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorPayload {

    private String error;
    private String errorCode;

    public String getError() {
        return this.error;
    }

    public void setError(String errorMsg) {
        this.error = errorMsg;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
