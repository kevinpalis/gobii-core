package org.gobiiproject.gobiimodel.dto.brapi.envelope;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrApiResultServerInfo<ServerInfoDTO> {

    private List<ServerInfoDTO> calls;

    public List<ServerInfoDTO> getCalls() {
        return this.calls;
    }

    public void setCalls(List<ServerInfoDTO> calls) {
        this.calls = calls;
    }
}
