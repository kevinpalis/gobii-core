package org.gobiiproject.gobiimodel.dto.brapi.envelope;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

@SuppressWarnings("rawtypes")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BrApiMetaData {

    private BrApiPagination pagination = new BrApiPagination();

    @JsonUnwrapped
    private List<BrApiStatus> status;

    private List datafiles;

    public BrApiPagination getPagination() {
        return this.pagination;
    }

    public void setPagination(BrApiPagination pagination) {
        this.pagination = pagination;
    }

    public List getStatus() {
        return this.status;
    }

    public void setStatus(List<BrApiStatus> status) {
        this.status = status;
    }

    public List getDatafiles() {
        return this.datafiles;
    }

    public void setDatafiles(List datafiles) {
        this.datafiles = datafiles;
    }
}
