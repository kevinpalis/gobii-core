package org.gobiiproject.gobiimodel.dto.brapi.envelope;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

import java.util.List;

@SuppressWarnings("rawtypes")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BrApiMasterListPayload<T>  extends BrApiMasterPayload {

    private BrApiResult<T> result = new BrApiResult<>();

    public BrApiMasterListPayload() {
    }

    public BrApiMasterListPayload(List<T> listData) {
        this.result.setData(listData);
    }

    public BrApiMasterListPayload(List<T> listData, Integer pageSize,
                                  Integer currentPage) {
        this.result.setData(listData);
        this.getMetadata().getPagination().setPageSize(pageSize);
        this.getMetadata().getPagination().setCurrentPage(currentPage);
    }

    public BrApiMasterListPayload(List<T> listData, Integer pageSize,
                                  String nextPageToken) {
        this.result.setData(listData);
        this.getMetadata().getPagination().setPageSize(pageSize);
        this.getMetadata().getPagination().setNextPageToken(nextPageToken);
    }

    public static <T> BrApiMasterListPayload<T> createPayload(PagedResult<T> pagedResult) {
        if(StringUtils.isNotEmpty(pagedResult.getNextPageToken())) {
            return new BrApiMasterListPayload<T>(
                pagedResult.getResult(),
                pagedResult.getCurrentPageSize(),
                pagedResult.getNextPageToken());
        }
        else {
            return new BrApiMasterListPayload<T>(
                pagedResult.getResult(),
                pagedResult.getCurrentPageSize(),
                pagedResult.getCurrentPageNum());
        }
    }


    public BrApiResult<T> getResult() {
        return this.result;
    }

    public void setResult(BrApiResult<T> result) {
        this.result = result;
    }


}
