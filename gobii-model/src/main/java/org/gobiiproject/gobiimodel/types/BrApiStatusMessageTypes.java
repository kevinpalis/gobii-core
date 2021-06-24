package org.gobiiproject.gobiimodel.types;

public enum BrApiStatusMessageTypes {

    INFO("INFO"),
    WARNING("WARNING");

    private String messageType;

    BrApiStatusMessageTypes(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return this.messageType;
    }
}
