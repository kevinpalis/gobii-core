package org.gobiiproject.gobiimodel.dto.noaudit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlleleMatricesSearchDTO {


    private List<String> markerProfileDbId;

    public List<String> getMarkerProfileDbId() {
        return markerProfileDbId;
    }

    public void setMarkerProfileDbId(List<String> markerProfileDbId) {
        this.markerProfileDbId = markerProfileDbId;
    }

    public List<String> getMatrixDbId() {
        return matrixDbId;
    }

    public void setMatrixDbId(List<String> matrixDbId) {
        this.matrixDbId = matrixDbId;
    }

    private List<String> matrixDbId;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    private String format;

}
