package org.gobiiproject.gobiimodel.dto.brapi.envelope;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.gobiiproject.gobiimodel.types.BrApiStatusMessageTypes;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrApiStatus {

    private BrApiStatusMessageTypes messageType;
    private String message;

    public BrApiStatusMessageTypes getMessageType() {
        return this.messageType;
    }

    public void setMessageType(BrApiStatusMessageTypes messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
