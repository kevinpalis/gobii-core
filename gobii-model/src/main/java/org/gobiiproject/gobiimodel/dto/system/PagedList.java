package org.gobiiproject.gobiimodel.dto.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PagedList<T> {


    public PagedList(Date queryTime, List<T> dtoList, Integer currentPageNo, Integer totalPages, String pgQueryId) {
        this.queryTime = queryTime;
        this.dtoList = dtoList;
        this.currentPageNo = currentPageNo;
        this.totalPages = totalPages;
        this.pgQueryId = pgQueryId;
    }

    private Date queryTime;
    private List<T> dtoList = new ArrayList<>();
    private Integer currentPageNo;
    private Integer totalPages;
    private String pgQueryId;

    public Date getQueryTime() {
        return queryTime;
    }

    public List<T> getDtoList() {
        return dtoList;
    }

    public Integer getCurrentPageNo() {
        return currentPageNo;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public String getPgQueryId() {
        return pgQueryId;
    }
}
