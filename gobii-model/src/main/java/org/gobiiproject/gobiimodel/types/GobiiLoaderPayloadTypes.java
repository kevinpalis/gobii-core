package org.gobiiproject.gobiimodel.types;

public enum GobiiLoaderPayloadTypes {

    MARKERS("markers"),
    SAMPLES("samples");

    private String payloadType;

    GobiiLoaderPayloadTypes(String payloadType) {
        this.payloadType = payloadType;
    }

    public String getTerm() {
        return this.payloadType;
    }
}
