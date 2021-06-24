package org.gobiiproject.gobiimodel.dto.system;

import java.util.ArrayList;
import java.util.List;

/**
 * Old model PagedList is poorly designed.
 * Creating this instead of modyfying it because, it has its own dependecies
 * which looks like crucial to structural integrity of GDM web service.
 * Since Refactoring the old one is not in  for the service which is being built now,
 * creating this new one.
 * @param <T>
 */
public class PagedResult<T> {

    public static <S> PagedResult<S> createFrom(Integer page, List<S> results) {
        PagedResult<S> result = new PagedResult<>();
        result.setCurrentPageNum(page);
        result.setCurrentPageSize(results.size());
        result.setResult(results);
        return result;
    }
    
    private List<T> result = new ArrayList<>();
    private String nextPageToken;
    private Integer currentPageNum;
    private Integer currentPageSize;

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public Integer getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(Integer currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    public Integer getCurrentPageSize() {
        return currentPageSize;
    }

    public void setCurrentPageSize(Integer currentPageSize) {
        this.currentPageSize = currentPageSize;
    }
}
