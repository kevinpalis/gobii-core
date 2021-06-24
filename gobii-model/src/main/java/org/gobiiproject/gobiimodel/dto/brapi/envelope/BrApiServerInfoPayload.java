package org.gobiiproject.gobiimodel.dto.brapi.envelope;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@SuppressWarnings("rawtypes")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BrApiServerInfoPayload<ServerInfoDTO>  extends BrApiMasterPayload {

    private BrApiResultServerInfo<ServerInfoDTO> result =
            new BrApiResultServerInfo<>();

    public BrApiServerInfoPayload(List<ServerInfoDTO> listCalls) {
        this.result.setCalls(listCalls);
    }

    public BrApiServerInfoPayload(List<ServerInfoDTO> listCalls,
                                  Integer pageSize,
                                  Integer currentPage) {
        this.result.setCalls(listCalls);
        this.getMetadata().getPagination().setPageSize(pageSize);
        this.getMetadata().getPagination().setCurrentPage(currentPage);
    }

    public BrApiResultServerInfo<ServerInfoDTO> getResult() {
        return this.result;
    }

    public void setResult(BrApiResultServerInfo<ServerInfoDTO> result) {
        this.result = result;
    }

}
