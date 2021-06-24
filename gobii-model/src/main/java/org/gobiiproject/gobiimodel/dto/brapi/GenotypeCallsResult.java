package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiResult;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenotypeCallsResult {

    public GenotypeCallsResult(String sepUnphased, String sepPhased,
                               String unknownString, List<GenotypeCallsDTO> data) {
        this.sepUnphased = sepUnphased;
        this.sepPhased = sepPhased;
        this.unknownString = unknownString;
        this.data = data;
    }

    private String sepUnphased;

    private String sepPhased;

    private String unknownString;

    private List<GenotypeCallsDTO> data;

    public String getSepUnphased() {
        return sepUnphased;
    }

    public void setSepUnphased(String sepUnphased) {
        this.sepUnphased = sepUnphased;
    }

    public String getSepPhased() {
        return sepPhased;
    }

    public void setSepPhased(String sepPhased) {
        this.sepPhased = sepPhased;
    }

    public String getUnknownString() {
        return unknownString;
    }

    public void setUnknownString(String unknownString) {
        this.unknownString = unknownString;
    }

    public List<GenotypeCallsDTO> getData() {
        return data;
    }

    public void setData(List<GenotypeCallsDTO> data) {
        this.data = data;
    }
}
