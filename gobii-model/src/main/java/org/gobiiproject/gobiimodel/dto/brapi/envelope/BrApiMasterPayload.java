package org.gobiiproject.gobiimodel.dto.brapi.envelope;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BrApiMasterPayload<T> {

    private BrApiMetaData metadata = new BrApiMetaData();
    private T result;

    public BrApiMasterPayload() {
    }

    public BrApiMasterPayload(T result) {
        this.result = result;
    }

    public BrApiMasterPayload(T result, Integer pageSize, Integer currentPage) {

        this.result = result;
        this.getMetadata().getPagination().setPageSize(pageSize);
        this.getMetadata().getPagination().setCurrentPage(currentPage);

    }

    public BrApiMasterPayload(T result, Integer pageSize, String pageToken) {

        this.result = result;
        this.getMetadata().getPagination().setPageSize(pageSize);
        this.getMetadata().getPagination().setNextPageToken(pageToken);

    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public BrApiMetaData getMetadata() {
        return this.metadata;
    }

    public void setMetadata(BrApiMetaData metaData) {
        this.metadata = metaData;
    }

}
